package models

import models.user.{UserFull, UserSetting, UserSettings}
import play.api.test.Helpers.inMemoryDatabase
import service.table.LoginsTable
import service.UserTest
import service.Login
import play.api.db.slick.Config.driver.simple._
import com.artclod.util.TryUtil._

object DBTest {

	val inMemH2 = inMemoryDatabase(options = Map("MODE" -> "PostgreSQL"))

  /**
   * Create a default fake user who has consented.
   */
	def newFakeUser(userNoId: Login)(implicit session: Session) : UserSetting = {
    val user = LoginsTable.insert(userNoId)

    val createSettings = (() => UserSettings.create(UserSetting(user.id, consented = true, name = UserSettings.validName(user.fullName), allowAutoMatch = true, seenHelp = true, emailGameUpdates = false)))
    val settings = createSettings.retryOnFail()

    settings.get
  }

	def newFakeUser(implicit session: Session) : UserSetting = newFakeUser(UserTest())

  /**
   * Create a default fake user who has not consented (and therefore cannot access most of the site).
   */
  def newFakeUserNoConsent(userNoId: Login)(implicit session: Session): Login = LoginsTable.insert(userNoId)

  def newFakeUserNoConsent(implicit session: Session): Login = newFakeUserNoConsent(UserTest())

}