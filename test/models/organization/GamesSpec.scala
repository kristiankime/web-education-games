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

	"requestCourse" should {

    "setup a request associated with the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val organization = Organizations.create(TestOrganization())
        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))
        val requestor = DBTest.newFakeUser(UserTest())
        val requestee = DBTest.newFakeUser(UserTest())

        Games.requestCourse(requestor.id, requestee.id, course.id)

        val gameRequests = Games.requests(requestee.id, course.id)
        val gameRequest = gameRequests(0)
        gameRequest.requestor must beEqualTo(requestor)
        gameRequest.requestee must beEqualTo(requestee)
        gameRequest.course must beSome(course)
      }
    }

  }

}
