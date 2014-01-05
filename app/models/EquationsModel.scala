package models

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

case class Equation(id: Long, equation: String)

object EquationsModel {
	val table = new EquationsTable

	def all() = DB.withSession { implicit session: Session =>
		Query(table).list
	}

	def create(equation: String): Long = DB.withSession { implicit session: Session =>
		table.autoInc.insert(equation)
	}

	def read(id: Long) = DB.withSession { implicit session: Session =>
		Query(table).where(_.id === id).firstOption
	}

	def delete(id: Long) = DB.withSession { implicit session: Session =>
		table.where(_.id === id).delete
	}
}

class EquationsTable extends Table[Equation]("equations") {
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def equation = column[String]("equation", O.NotNull)
	def * = id ~ equation <> (Equation, Equation.unapply _)
	def autoInc = equation returning id
}
