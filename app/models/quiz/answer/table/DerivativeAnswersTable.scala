package models.quiz.answer.table

import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC._
import models.quiz.answer.DerivativeAnswer
import models.quiz.table.{AnswerIdNext, derivativeQuestionsTable}
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.lifted.ForeignKeyQuery
import scala.slick.model.ForeignKeyAction

class DerivativeAnswersTable(tag: Tag) extends Table[DerivativeAnswer](tag, "derivative_answers") with  AnswersTable[DerivativeAnswer] {
	def mathML = column[MathMLElem]("mathml")
	def rawStr = column[String]("rawstr")

	def * = (id, ownerId, questionId, mathML, rawStr, comment, correct, creationDate) <> (DerivativeAnswer.tupled, DerivativeAnswer.unapply _)

  def idFK  = foreignKey("derivative_answers__id_fk", id, AnswerIdNext.answerIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("derivative_answers__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionFK = foreignKey("derivative_answers__question_fk", questionId, derivativeQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
