package models.question.table

import models.question.derivative.table._
import models.support._
import models.support.table.UserLinkRow
import play.api.db.slick.Config.driver.simple._
import service._
import service.table._

import scala.slick.model.ForeignKeyAction

object QuestionIdNext {
	val questionIdTable = TableQuery[QuestionIdTable]

	def apply()(implicit session: Session) : QuestionId =
		(questionIdTable returning questionIdTable.map(_.questionId)) += QuestionId(-1)
}

class QuestionIdTable(tag: Tag) extends Table[QuestionId](tag, "question_id") {
	def questionId = column[QuestionId]("question_id", O.PrimaryKey, O.AutoInc)
	def * = questionId
}