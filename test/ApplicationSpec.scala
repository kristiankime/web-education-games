import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import com.artclod.securesocial.TestUtils._
import models.DBTest
import service.UserTmpTest
import securesocial.core.PasswordInfo
import service.SlickUserService
import securesocial.core.Identity
import securesocial.core.IdGenerator

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

	"Application" should {

		"send 404 on a bad request" in new WithApplication {
			route(FakeRequest(GET, "/boum")) must beNone
		}

		"render the index page" in new WithApplication {
			val user = DBTest.fakeUser(UserTmpTest())
			val home = route(FakeRequest(GET, "/").withLoggedInUser(user)).get

			status(home) must equalTo(OK)
			contentType(home) must beSome.which(_ == "text/html")
			contentAsString(home) must contain("Welcome to the EdTech server")
		}
	}
}
