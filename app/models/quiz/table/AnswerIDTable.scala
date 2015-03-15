package models.quiz.table

import models.support._
import play.api.db.slick.Config.driver.simple._

object AnswerIdNext {
	val answerIdTable = TableQuery[AnswerIdTable]

	def apply()(implicit session: Session) : AnswerId =
		(answerIdTable returning answerIdTable.map(_.id)) += AnswerIdRow(null)
}

class AnswerIdTable(tag: Tag) extends Table[AnswerIdRow](tag, "answer_id") {
	def id = column[AnswerId]("id", O.PrimaryKey, O.AutoInc)
	def dummy = column[Short]("dummy") // LATER This is here to fix an insert bug with Slick + Postgres, it can be removed when that bug is fixed
	def * = (id, dummy) <> (AnswerIdRow.tupled, AnswerIdRow.unapply _)
}

case class AnswerIdRow(id: AnswerId, dummy: Short = 0)