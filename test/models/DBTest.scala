package models

//import play.api.db.slick.DB
import scala.slick.session.Session
import play.api.test.Helpers.inMemoryDatabase
import play.api.test.FakeApplication
import service.UserTmp
import play.api.Play.current
import play.api.db.slick.DB
import service.table.UserTable
import service.UserTmpTest
import service.User

object DBTest {

	val inMemH2 = inMemoryDatabase(options = Map("MODE" -> "PostgreSQL"))

	def fakeUser(data: UserTmp)(implicit session: Session): User = (new UserTable).insert(data)

	def fakeUser(implicit session: Session): User = fakeUser(UserTmpTest())
}