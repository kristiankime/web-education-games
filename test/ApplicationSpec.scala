import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

	// http://stackoverflow.com/questions/5762246/playframework-secure-module-how-do-you-log-in-to-test-a-secured-controller-in
	"Application" should {

		skipAll

		"send 404 on a bad request" in new WithApplication {
			route(FakeRequest(GET, "/boum")) must beNone
		}

		"render the index page" in new WithApplication {
			val home = route(FakeRequest(GET, "/")).get

			status(home) must equalTo(OK)
			contentType(home) must beSome.which(_ == "text/html")
			contentAsString(home) must contain("Welcome to the EdTech server")
		}
	}
}
