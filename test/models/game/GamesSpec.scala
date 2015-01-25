package models.game

import models.DBTest.{inMemH2, newFakeUser}
import models.game.GameRole._
import models.organization._
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test.{FakeApplication, _}
import service._

// TODO check out http://workwithplay.com/blog/2013/06/19/integration-testing/
@RunWith(classOf[JUnitRunner])
class GamesSpec extends Specification {

  private def organizationAndCourse(implicit session: Session) = {
    val organization = Organizations.create(TestOrganization())
    val course = Courses.create(TestCourse(owner = newFakeUser.id, organizationId = organization.id))
    (organization, course)
  }

  "studentsToPlayWith" should {
    "return none if there are no other students in the class" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (organization, course) = organizationAndCourse
        val requestingStudent = newFakeUser
        Courses.grantAccess(course, View)(requestingStudent, session)

        Games.studentsToPlayWith(requestingStudent.id, course.id) must beEmpty
      }
    }

    "return other students in the class" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (organization, course) = organizationAndCourse
        val (requestingStudent, student1, student2) = (newFakeUser, newFakeUser, newFakeUser)
        Courses.grantAccess(course, View)(requestingStudent, session)
        Courses.grantAccess(course, View)(student1, session)
        Courses.grantAccess(course, View)(student2, session)

        Games.studentsToPlayWith(requestingStudent.id, course.id).toSet must beEqualTo(Set(student1, student2))
      }
    }

    "exclude student if they are already in a game with the user" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (organization, course) = organizationAndCourse
        val (requestingStudent, student1, student2) = (newFakeUser, newFakeUser, newFakeUser)
        Courses.grantAccess(course, View)(requestingStudent, session)
        Courses.grantAccess(course, View)(student1, session)
        Courses.grantAccess(course, View)(student2, session)

        Games.request(requestingStudent, student2)

        Games.studentsToPlayWith(requestingStudent.id, course.id).toSet must beEqualTo(Set(student1))
      }
    }
  }

  "request" should {
    "setup a request associated with the two users" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (requestor, requestee) = (newFakeUser, newFakeUser)

        Games.request(requestor, requestee)

        val gameRequests = Games.requests(requestee.id)
        val gameRequest = gameRequests(0)
        gameRequest.requestor.user must beEqualTo(requestor)
        gameRequest.requestee.user must beEqualTo(requestee)
        gameRequest.course must beNone
      }
    }
  }

  "request (course)" should {
    "setup a request associated with the two users and the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (organization, course) = organizationAndCourse
        val (requestor, requestee) = (newFakeUser, newFakeUser)

        Games.request(requestor, requestee, course)

        val gameRequests = Games.requests(requestee.id, course.id)
        val gameRequest = gameRequests(0)
        gameRequest.requestor.user must beEqualTo(requestor)
        gameRequest.requestee.user must beEqualTo(requestee)
        gameRequest.course must beSome(course)
      }
    }
  }

  "activeGame" should {
    "return none if the two users have never started a game" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (requestor, requestee) = (newFakeUser, newFakeUser)

        Games.activeGame(requestor.id, requestee.id) must beEmpty
      }
    }

    "return a game if the users have started one (requestor first)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (requestor, requestee) = (newFakeUser, newFakeUser)
        val game = Games.request(requestor, requestee)

        Games.activeGame(requestor.id, requestee.id).map(_.id) must beSome(game.id)
      }
    }

    "return a game if the users have started one (requestee first)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (requestee, requestor) = (newFakeUser, newFakeUser)
        val game = Games.request(requestor, requestee)

        Games.activeGame(requestor.id, requestee.id).map(_.id) must beSome(game.id)
      }
    }

    "return none if the users have finished a game (requestor first)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (requestor, requestee) = (newFakeUser, newFakeUser)
        val game = Games.request(requestor, requestee)

        TestGame.finish(game)

        Games.activeGame(requestor.id, requestee.id).map(_.id) must beNone
      }
    }

    "return none if the users have finished a game (requestee first)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (requestee, requestor) = (newFakeUser, newFakeUser)
        val game = Games.request(requestor, requestee)

        TestGame.finish(game)

        Games.activeGame(requestor.id, requestee.id).map(_.id) must beNone
      }
    }
  }

  "active" should {
    "find active games" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (organization, course) = organizationAndCourse
        val (requestor, requestee) = (newFakeUser, newFakeUser)

        val gameInProgress = Games.request(requestor, requestee, course)
        Games.update(gameInProgress.toState.asInstanceOf[GameRequested].accept(requestee.id))

        val gameRequest = Games.active(requestee.id)(session)(0)

        gameRequest.requestor.user must beEqualTo(requestor)
        gameRequest.requestee.user must beEqualTo(requestee)
        gameRequest.course must beSome(course)
        gameRequest must beEqualTo(Games.active(requestor.id)(session)(0)) // ensure Tor vs Tee is the same
      }
    }

    "ignore rejected games" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (organization, course) = organizationAndCourse
        val (requestor, requestee) = (newFakeUser, newFakeUser)

        val gameDone = Games.request(requestor, requestee, course)
        Games.update(gameDone.toState.asInstanceOf[GameRequested].reject(requestee.id))

        Games.active(requestor.id)(session) must beEmpty
        Games.active(requestee.id)(session) must beEmpty
      }
    }

    "ignore finished games" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (organization, course) = organizationAndCourse
        val (requestor, requestee) = (newFakeUser, newFakeUser)

        val game = Games.request(requestor, requestee, course)
        TestGame.finish(game)

        Games.active(requestor.id)(session) must beEmpty
        Games.active(requestee.id)(session) must beEmpty
      }
    }
  }

  "gameRole" should {

    "return correct role" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (requestor, requestee, other) = (newFakeUser, newFakeUser, newFakeUser)
        val game = Games.request(requestor, requestee)

        game.gameRole(requestor) must beEqualTo(Requestor)
        game.gameRole(requestee) must beEqualTo(Requestee)
        game.gameRole(other) must beEqualTo(Unrelated)
      }
    }

  }

}
