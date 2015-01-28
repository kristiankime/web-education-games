package controllers

import com.artclod.securesocial.TestUtils._
import models.DBTest._
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test.Helpers._
import play.api.test._
import service.Logins

@RunWith(classOf[JUnitRunner])
class HomeSpec extends Specification {

  "index" should {

    "render home page if the user has consented" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) { DB.withSession { implicit session: Session =>
      val response = route(FakeRequest(GET, "/").withLoggedInUser(Logins(newFakeUser.id).get)).get

      val statusVal = status(response)
      val redirectLocationVal = redirectLocation(response)

      status(response) must equalTo(OK)
      contentType(response) must beSome.which(_ == "text/html")
      contentAsString(response) must contain(viewsupport.ViewText.welcome)
    } }

    "redirect to consent form if the user has not consented" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) { DB.withSession { implicit session: Session =>
      val response = route(FakeRequest(GET, "/").withLoggedInUser(newFakeUserNoConsent)).get

      val statusVal = status(response)
      val redirectLocationVal = redirectLocation(response)

      status(response) must equalTo(SEE_OTHER)
      redirectLocation(response).map(_ must equalTo("/consent?goTo=%2F")) getOrElse failure("missing redirect location")
    } }

  }

}
