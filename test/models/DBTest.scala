package models

import play.api.test.Helpers.inMemoryDatabase
import play.api.test.FakeApplication
import play.api.Play.current
import play.api.db.slick.DB
import service.table.UsersTable
import service.UserTmpTest
import service.User
import play.api.db.slick.Config.driver.simple._


object DBTest {

	val inMemH2 = inMemoryDatabase(options = Map("MODE" -> "PostgreSQL"))

	def fakeUser(user: User)(implicit session: Session): User = UsersTable.insert(user)

	def fakeUser(implicit session: Session): User = fakeUser(UserTmpTest())
}