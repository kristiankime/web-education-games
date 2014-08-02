package models.user

import models.DBTest._
import models.Equations
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.db.slick._
import play.api.test.{FakeApplication, WithApplication}
import com.artclod.util.TryUtil._

import scala.collection.Bag
import scala.collection.parallel.CompositeThrowable
import scala.util.{Failure, Success}
import play.api.Logger

@RunWith(classOf[JUnitRunner])
class UserSettingsSpec extends Specification {

  "validName" should {

    "create a unique name" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {  DB.withSession { implicit session: Session =>
      val user1 = newFakeUserNoConsent
      val user1Settings = UserSetting(userId = user1.id, name = "name")
      UserSettings.create(user1Settings)

      val newName = UserSettings.validName(user1Settings.name)

      newName mustNotEqual(user1Settings.name)
    }  }


    "create multiple unique names, in series" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {

      val settings = DB.withSession { implicit session: Session =>
        val firstUser = newFakeUserNoConsent
        val firstUserSettings = UserSetting(userId = firstUser.id, name = "name")
        UserSettings.create(firstUserSettings)
        firstUserSettings
      }

      val nameSeq = for(i <- 1 to 20) yield {
        DB.withSession { implicit session: Session =>
          val newUserName = UserSettings.validName(settings.name)
          newUserName mustNotEqual (settings.name)
          val newUser = newFakeUserNoConsent
          val newUserSettings = UserSetting(userId = newUser.id, name = newUserName)
          UserSettings.create(newUserSettings).get.name
        }
      }
      val nameSet = nameSeq.toSet
      for(name <- nameSet) { nameSeq.filter(_ == name).size must beEqualTo(1) }
    }

    "create multiple unique names, even when done in parallel" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {

      val settings = DB.withSession { implicit session: Session =>
        val firstUser = newFakeUserNoConsent
        val firstUserSettings = UserSetting(userId = firstUser.id, name = "name")
        UserSettings.create(firstUserSettings)
        firstUserSettings
      }

      val nameSeq = (for(i <- 1 to 20) yield { () =>
        DB.withSession { implicit session: Session =>
          val newUserName = UserSettings.validName(settings.name)
          newUserName mustNotEqual (settings.name)
          val newUser = newFakeUserNoConsent
          val newUserSettings = UserSetting(userId = newUser.id, name = newUserName)
          UserSettings.create(newUserSettings)
        }
      }).par.map(_.retryOnFail(10)).seq.map(_.get.name) // run in parallel but also retry on fail
      val nameSet = nameSeq.toSet
      for(name <- nameSet) { nameSeq.filter(_ == name).size must beEqualTo(1) }
    }

  }

}
