package models

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

case class Equation(id: Long, equation: String)

object EquationsModel {
	val table = new EquationsTable

	def all()(implicit s: Session) = Query(table).list

	def create(equation: String)(implicit s:Session): Long = table.autoInc.insert(equation)
	
	def read(id: Long)(implicit s:Session) = Query(table).where(_.id === id).firstOption
	
	def delete(id: Long)(implicit s: Session) = table.where(_.id === id).delete
}

class EquationsTable extends Table[Equation]("equations") {
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def equation = column[String]("equation", O.NotNull)
	def * = id ~ equation <> (Equation, Equation.unapply _)
	def autoInc = equation returning id
}