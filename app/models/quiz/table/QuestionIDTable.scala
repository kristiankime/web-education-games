package models.quiz.table

import models.support._
import play.api.db.slick.Config.driver.simple._

object QuestionIdNext {
	val questionIdTable = TableQuery[QuestionIdTable]

	def apply()(implicit session: Session) : QuestionId =
		(questionIdTable returning questionIdTable.map(_.id)) += QuestionIdRow(null)
}

class QuestionIdTable(tag: Tag) extends Table[QuestionIdRow](tag, "question_id") {
	def id = column[QuestionId]("id", O.PrimaryKey, O.AutoInc)
	def dummy = column[Short]("dummy") // LATER This is here to fix an insert bug with Slick + Postgres, it can be removed when that bug is fixed
	def * = (id, dummy) <> (QuestionIdRow.tupled, QuestionIdRow.unapply _)
}

case class QuestionIdRow(id: QuestionId, dummy: Short = 0)