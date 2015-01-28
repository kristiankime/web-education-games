package controllers.organization

import com.artclod.securesocial.TestUtils._
import models.DBTest.newFakeUser
import models.organization._
import models.user.Logins
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test.Helpers._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class CoursesControllerSpec extends Specification {

  "Courses" should {

    "allow any user to view a course" in new WithApplication {
      DB.withSession {
        implicit session: Session =>
          val user = newFakeUser
          val organization = Organizations.create(TestOrganization())
          val course = Courses.create(TestCourse(owner = newFakeUser.id, organizationId = organization.id))

          val page = route(FakeRequest(GET, "/orgs/" + organization.id.v + "/courses/" + course.id.v).withLoggedInUser(Logins(user.id).get)).get

          status(page) must equalTo(OK)
          contentType(page) must beSome.which(_ == "text/html")
          contentAsString(page) must contain(course.name)
      }
    }

    "allow any user to add a course" in new WithApplication {
      DB.withSession {
        implicit session: Session =>
          val user = newFakeUser
          val organization = Organizations.create(TestOrganization())
          val page = route(FakeRequest(GET, "/orgs/" + organization.id.v + "/courses/create").withLoggedInUser(Logins(user.id).get)).get

          status(page) must equalTo(OK)
          contentType(page) must beSome.which(_ == "text/html")
          contentAsString(page) must contain("Please")
      }
    }

  }

}
