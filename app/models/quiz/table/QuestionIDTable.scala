package models.quiz.table

import models.support._
import play.api.db.slick.Config.driver.simple._

object QuestionIdNext {
	val questionIdTable = TableQuery[QuestionIdTable]

	def apply()(implicit session: Session) : QuestionId =
		(questionIdTable returning questionIdTable.map(_.id)) += QuestIdRow(null)
}

class QuestionIdTable(tag: Tag) extends Table[QuestIdRow](tag, "question_id") {
	def id = column[QuestionId]("id", O.PrimaryKey, O.AutoInc)
	def dummy = column[Short]("dummy")
	def * = (id, dummy) <> (QuestIdRow.tupled, QuestIdRow.unapply _)
}

case class QuestIdRow(id: QuestionId, dummy: Short = 0)