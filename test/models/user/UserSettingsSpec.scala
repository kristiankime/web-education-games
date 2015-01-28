package models.user

import models.DBTest._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.db.slick._
import play.api.test.{FakeApplication, WithApplication}
import com.artclod.util.TryUtil._

@RunWith(classOf[JUnitRunner])
class UserSettingsSpec extends Specification {

  // LATER We are not currently requiring unique names
  skipAll

  "validName" should {

    "return the starting name if it does not conflict" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {  DB.withSession { implicit session: Session =>
      val newName = Users.validName("name")

      newName must beEqualTo("name")
    }  }

    "create a unique name if the name already exists" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {  DB.withSession { implicit session: Session =>
      val user1 = newFakeUserNoConsent
      val user1Settings = User(id = user1.id, name = "name")
      Users.create(user1Settings)

      val newName = Users.validName(user1Settings.name)

      newName mustNotEqual(user1Settings.name)
    }  }


    "create multiple unique names, in series" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {

      val settings = DB.withSession { implicit session: Session =>
        val firstUser = newFakeUserNoConsent
        val firstUserSettings = User(id = firstUser.id, name = "name")
        Users.create(firstUserSettings)
        firstUserSettings
      }

      val nameSeq = for(i <- 1 to 20) yield {
        DB.withSession { implicit session: Session =>
          val newUserName = Users.validName(settings.name)
          newUserName mustNotEqual (settings.name)
          val newUser = newFakeUserNoConsent
          val newUserSettings = User(id = newUser.id, name = newUserName)
          Users.create(newUserSettings).get.name
        }
      }
      val nameSet = nameSeq.toSet
      for(name <- nameSet) { nameSeq.filter(_ == name).size must beEqualTo(1) }
    }

    "create multiple unique names, even when done in parallel" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {

      val settings = DB.withSession { implicit session: Session =>
        val firstUser = newFakeUserNoConsent
        val firstUserSettings = User(id = firstUser.id, name = "name")
        Users.create(firstUserSettings)
        firstUserSettings
      }

      val nameSeq = (for(i <- 1 to 20) yield { () =>
        DB.withSession { implicit session: Session =>
          val newUserName = Users.validName(settings.name)
          newUserName mustNotEqual (settings.name)
          val newUser = newFakeUserNoConsent
          val newUserSettings = User(id = newUser.id, name = newUserName)
          Users.create(newUserSettings)
        }
      }).par.map(_.retryOnFail(10)).seq.map(_.get.name) // run in parallel but also retry on fail
      val nameSet = nameSeq.toSet
      for(name <- nameSet) { nameSeq.filter(_ == name).size must beEqualTo(1) }
    }

  }

}
