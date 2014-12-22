package com.artclod.javascript

import com.artclod.securesocial.TestUtils._
import models.DBTest._
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.test.Helpers._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class RunJSSpec extends Specification {

  "loadJSLibrary" should {
    "return tex for " in new WithApplication() {
      RunJS.mathJS2Tex("2 + 3") must beEqualTo("{2}+{3}")
    }
  }


}
