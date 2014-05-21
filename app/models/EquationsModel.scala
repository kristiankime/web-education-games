package models

import play.api.db.slick.Config.driver.simple._

case class Equation(id: Long, equation: String)

class EquationsTable(tag: Tag) extends Table[Equation](tag, "equations") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def equation = column[String]("equation", O.NotNull)

  def * = (id, equation) <> (Equation.tupled, Equation.unapply _)
}

object EquationsModel {
	val table = TableQuery[EquationsTable]

	def all()(implicit session: Session) = table.list

	def create(equation: String)(implicit session: Session): Long = (table returning table.map(_.id)) += Equation(-1L, equation)

	def read(id: Long)(implicit session: Session) =	table.where(_.id === id).firstOption

	def delete(id: Long)(implicit session: Session) = table.where(_.id === id).delete
}


