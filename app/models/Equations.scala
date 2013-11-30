package models

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

case class Equation(id: Option[Long], equation: String)

object Equations {
	val Equations = new Equations

	def all()(implicit s: Session) = Query(Equations).list

	def create(equation: Equation)(implicit s: Session) = Equations.autoInc.insert(equation)

	def delete(id: Long)(implicit s: Session) = Equations.where(_.id === id).delete

}

class Equations extends Table[Equation]("equations") {
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def equation = column[String]("equation", O.NotNull)
	def * = id.? ~ equation <> (Equation, Equation.unapply _)
	def autoInc = * returning id
}
