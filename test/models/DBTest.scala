package models

import play.api.db.slick.DB
import scala.slick.session.Session

object DBTest {

	def withSessionAndRollback[T](f: Session => T): T = {
		import play.api.Play.current
		DB.withSession { implicit s: Session =>
			s.withTransaction {
				val r = f(s)
				s.rollback
				r
			}
		}
	}

}