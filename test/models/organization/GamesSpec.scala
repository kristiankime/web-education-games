package models.organization

import models.DBTest
import models.DBTest.inMemH2
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test.{FakeApplication, _}
import service.{UserTest, _}

// TODO check out http://workwithplay.com/blog/2013/06/19/integration-testing/
@RunWith(classOf[JUnitRunner])
class GamesSpec extends Specification {

  "request" should {
    "setup a request associated with the two users" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val requestor = DBTest.newFakeUser(UserTest())
        val requestee = DBTest.newFakeUser(UserTest())

        Games.request(requestor.id, requestee.id)

        val gameRequests = Games.requests(requestee.id)
        val gameRequest = gameRequests(0)
        gameRequest.requestor must beEqualTo(requestor)
        gameRequest.requestee must beEqualTo(requestee)
        gameRequest.course must beNone
      }
    }
  }

	"request (course)" should {
    "setup a request associated with the two users and the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val organization = Organizations.create(TestOrganization())
        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))
        val requestor = DBTest.newFakeUser(UserTest())
        val requestee = DBTest.newFakeUser(UserTest())

        Games.request(requestor.id, requestee.id, course.id)

        val gameRequests = Games.requests(requestee.id, course.id)
        val gameRequest = gameRequests(0)
        gameRequest.requestor must beEqualTo(requestor)
        gameRequest.requestee must beEqualTo(requestee)
        gameRequest.course must beSome(course)
      }
    }
  }


  "activeGame" should {

    "return none if the two users have never started a game" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val requestor = DBTest.newFakeUser(UserTest())
        val requestee = DBTest.newFakeUser(UserTest())

        val activeGame = Games.activeGame(requestor.id, requestee.id)
        activeGame must beEmpty
      }
    }

    "return a game if the users have started one" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val requestor = DBTest.newFakeUser(UserTest())
        val requestee = DBTest.newFakeUser(UserTest())

        val game = Games.request(requestor.id, requestee.id)

        val activeGame = Games.activeGame(requestor.id, requestee.id)
        activeGame.map(_.id) must beSome(game.id)
      }
    }

  }

}
