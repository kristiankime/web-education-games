import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import com.artclod.securesocial.TestUtils._
import models.DBTest
import models.DBTest.fakeUser
import models.organization.Courses
import models.organization.CourseTmpTest
import service.{UserTmpTest => U}
import securesocial.core.PasswordInfo
import service.SlickUserService
import securesocial.core.Identity
import securesocial.core.IdGenerator
import play.api.db.slick.DB
import scala.slick.session.Session

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

	"Application" should {

		"send 404 on a bad request" in new WithApplication {
			route(FakeRequest(GET, "/boum")) must beNone
		}

		"render the index page" in new WithApplication {
			DB.withSession { implicit session: Session =>
				val user = fakeUser
				val home = route(FakeRequest(GET, "/").withLoggedInUser(user)).get

				status(home) must equalTo(OK)
				contentType(home) must beSome.which(_ == "text/html")
				contentAsString(home) must contain("Welcome to the EdTech server")
			}
		}
	}

	"Courses" should {

		"send allow any user to view a course" in new WithApplication {
			DB.withSession { implicit session: Session =>
				val user = fakeUser
				val course = Courses.create(CourseTmpTest(owner = fakeUser.id))
				
				val home = route(FakeRequest(GET, "/courses/" + course.id.v).withLoggedInUser(user)).get
			}
		}

	}
}
