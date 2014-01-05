package models

import play.api.db.slick.DB
import scala.slick.session.Session
import play.api.test.Helpers.inMemoryDatabase
import play.api.test.FakeApplication
import service.table.UserTmp
import play.api.Play.current
import play.api.db.slick.DB
import service.table.UserTable

object DBTest {

	val inMemH2 = inMemoryDatabase(options = Map("MODE" -> "PostgreSQL"))

	def fakeUser(data: UserTmp) = DB.withSession { implicit session: Session =>
		UserTable.save(data)
	}

}