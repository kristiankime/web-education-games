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
class GameSpec extends Specification {

  private def organizationAndCourse(implicit session: Session) = {
    val organization = Organizations.create(TestOrganization())
    val course = Courses.create(TestCourse(owner = newFakeUser.id, organizationId = organization.id))
    (organization, course)
  }

  "isTeacher" should {
    "return true if user has write access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (organization, course) = organizationAndCourse
        val (requestee, requestor, teacher) = (newFakeUser, newFakeUser, newFakeUser)
        val game = Games.request(requestor, requestee, course)

        Courses.grantAccess(course, Edit)(teacher, session)

        game.isTeacher(teacher, session) must beTrue
      }
    }

    "return false if user has read access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (organization, course) = organizationAndCourse
        val (requestee, requestor, teacher) = (newFakeUser, newFakeUser, newFakeUser)
        val game = Games.request(requestor, requestee, course)

        Courses.grantAccess(course, View)(teacher, session)

        game.isTeacher(teacher, session) must beFalse
      }
    }

    "return false if user has no access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val (organization, course) = organizationAndCourse
        val (requestee, requestor, teacher) = (newFakeUser, newFakeUser, newFakeUser)
        val game = Games.request(requestor, requestee, course)

        game.isTeacher(teacher, session) must beFalse
      }
    }
  }

}
