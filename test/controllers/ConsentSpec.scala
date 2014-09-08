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
import service.{UserTest, User}

@RunWith(classOf[JUnitRunner])
class ConsentSpec extends Specification {

  "startingName" should {

    "use email name if one exists" in  {
      Consent.startingName(UserTest(email=Some("emailname@test"))) must beEqualTo("emailname")
    }

    "use full name if there is no email" in  {
      Consent.startingName(UserTest(fullName = "full name")) must beEqualTo("full name")
    }

  }

}
