package models.quiz.question.table

import com.artclod.mathml.scalar._
import com.artclod.mathml.slick.MathMLMapper._
import models.quiz.question.support.DerivativeOrder
import models.quiz.question.support.DerivativeOrder.string2DerivativeOrder
import models.quiz.question.{DerivativeGraphQuestion, DerivativeQuestion}
import models.quiz.table.{QuestionIdNext, quizzesTable}
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class DerivativeGraphQuestionsTable(tag: Tag) extends Table[DerivativeGraphQuestion](tag, "derivative_graph_questions") with QuestionsTable[DerivativeGraphQuestion] {
	def mathML = column[MathMLElem]("mathml")
	def rawStr = column[String]("rawstr")
	def derivativeOrder = column[DerivativeOrder]("derivativeOrder")

	def * = (id, ownerId, mathML, rawStr, creationDate, derivativeOrder, atCreationDifficulty, quizId, order) <> (DerivativeGraphQuestion.tupled, DerivativeGraphQuestion.unapply _)

	def idFK = foreignKey("derivative_graph_questions__id_fk", id, QuestionIdNext.questionIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("derivative_graph_questions__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("derivative_graph_questions__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
