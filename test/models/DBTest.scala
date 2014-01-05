package models

import play.api.db.slick.DB
import scala.slick.session.Session
import play.api.test.Helpers.inMemoryDatabase
import play.api.test.FakeApplication


object DBTest {

	val inMemH2 = inMemoryDatabase(options = Map("MODE" -> "PostgreSQL"))
//	val appInMemH2 = FakeApplication(additionalConfiguration = inMemH2)
//	
//	def withSessionAndRollback[T](f: Session => T): T = {
//		import play.api.Play.current
//		DB.withSession { implicit s: Session =>
//			s.withTransaction {
//				val r = f(s)
//				s.rollback
//				r
//			}
//		}
//	}

}