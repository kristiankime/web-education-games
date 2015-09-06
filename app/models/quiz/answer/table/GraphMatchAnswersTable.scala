package models.quiz.answer.table

import models.quiz.answer.{GraphMatchAnswer}
import models.quiz.table.{AnswerIdNext, graphMatchQuestionsTable}
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class GraphMatchAnswersTable(tag: Tag) extends Table[GraphMatchAnswer](tag, "graph_match_answers") with AnswersTable[GraphMatchAnswer] {
	def guessIndex = column[Short]("guess_index")

	def * = (id, ownerId, questionId, guessIndex, correct, creationDate) <> (GraphMatchAnswer.tupled, GraphMatchAnswer.unapply _)

  def idFK  = foreignKey("graph_match_answers__id_fk", id, AnswerIdNext.answerIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("graph_match_answers__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionFK = foreignKey("graph_match_answers__question_fk", questionId, graphMatchQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
