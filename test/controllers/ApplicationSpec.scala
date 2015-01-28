package controllers

import com.artclod.securesocial.TestUtils._
import models.DBTest._
import models.user.Logins
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test.Helpers._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "render the index page (when logged in)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) { DB.withSession { implicit session: Session =>
          val response = route(FakeRequest(GET, "/").withLoggedInUser(Logins(newFakeUser.id).get)).get

          status(response) must equalTo(OK)
          contentType(response) must beSome.which(_ == "text/html")
          contentAsString(response) must contain(viewsupport.ViewText.welcome)
    } }

//        "bad routes should back out to next " in new WithApplication {
//          route(FakeRequest(GET, "/boum")) must beNone
//        }
  }


  "untrail" should {
    "indicated trailing / are not used in the application" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) { DB.withSession { implicit session: Session =>
      val response = route(FakeRequest(GET, "/boum/").withLoggedInUser(Logins(newFakeUser.id).get)).get

      status(response) must equalTo(MOVED_PERMANENTLY)
      redirectLocation(response).map(_ must equalTo("/boum")) getOrElse failure("missing redirect location")
    } }
  }

  "backTrack" should {
    "redirect back to a working url" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) { DB.withSession { implicit session: Session =>
      val response = route(FakeRequest(GET, "/boum").withLoggedInUser(Logins(newFakeUser.id).get)).get

      status(response) must equalTo(SEE_OTHER)
      redirectLocation(response).map(_ must equalTo("/")) getOrElse failure("missing redirect location")
    } }
  }

}
