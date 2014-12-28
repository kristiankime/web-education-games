package models

import com.artclod.slick.JodaUTC
import models.question.derivative.table._
import models.support.{UserId, QuestionId}
import models.user.{UserSetting, UserSettings}
import org.joda.time.DateTime
import play.api.test.Helpers.inMemoryDatabase
import play.api.test.FakeApplication
import play.api.Play.current
import play.api.db.slick.DB
import service.table.UsersTable
import service.UserTest
import service.User
import play.api.db.slick.Config.driver.simple._
import com.artclod.util.TryUtil._

object DBTest {

	val inMemH2 = inMemoryDatabase(options = Map("MODE" -> "PostgreSQL"))

  /**
   * Create a default fake user who has consented.
   *
   * @param userNoId
   * @param session
   * @return
   */
	def newFakeUser(userNoId: User)(implicit session: Session): User = {
    val user = UsersTable.insert(userNoId)

    val createSettings = (() => UserSettings.create(UserSetting(user.id, consented = true, name = UserSettings.validName(user.fullName), allowAutoMatch = true, seenHelp = true, emailGameUpdates = false)))
    createSettings.retryOnFail()

    user
  }

	def newFakeUser(implicit session: Session): User = newFakeUser(UserTest())



  /**
   * Create a default fake user who has not consented (and therefore cannot access most of the site).
   *
   * @param userNoId
   * @param session
   * @return
   */
  def newFakeUserNoConsent(userNoId: User)(implicit session: Session): User = UsersTable.insert(userNoId)

  def newFakeUserNoConsent(implicit session: Session): User = newFakeUserNoConsent(UserTest())

}