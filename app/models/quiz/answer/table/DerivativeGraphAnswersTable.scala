package models.quiz.answer.table

import com.artclod.mathml.scalar._
import models.quiz.answer.{DerivativeGraphAnswer, DerivativeAnswer}
import models.quiz.question.support.DerivativeOrder
import models.quiz.question.support.DerivativeOrder.string2DerivativeOrder
import models.quiz.table.{AnswerIdNext, derivativeGraphQuestionsTable}
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class DerivativeGraphAnswersTable(tag: Tag) extends Table[DerivativeGraphAnswer](tag, "derivative_graph_answers") with  AnswersTable[DerivativeGraphAnswer] {
	def derivativeOrder = column[DerivativeOrder]("derivative_order")

	def * = (id, ownerId, questionId, derivativeOrder, comment, correct, creationDate) <> (DerivativeGraphAnswer.tupled, DerivativeGraphAnswer.unapply _)

  def idFK  = foreignKey("derivative_graph_answers__id_fk", id, AnswerIdNext.answerIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("derivative_graph_answers__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionFK = foreignKey("derivative_graph_answers__question_fk", questionId, derivativeGraphQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
