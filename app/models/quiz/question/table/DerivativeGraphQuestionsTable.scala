package models.quiz.question.table

import com.artclod.mathml.scalar._
import models.quiz.question.support.DerivativeOrder
import models.quiz.question.support.DerivativeOrder.string2DerivativeOrder
import models.quiz.question.{Question2Quiz, DerivativeGraphQuestion, DerivativeQuestion}
import models.quiz.table.{QuestionIdNext, quizzesTable, derivativeGraphQuestionsTable}
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable
import models.support._

import scala.slick.model.ForeignKeyAction

class DerivativeGraphQuestionsTable(tag: Tag) extends Table[DerivativeGraphQuestion](tag, "derivative_graph_questions") with QuestionsTable[DerivativeGraphQuestion] {
	def mathML = column[MathMLElem]("mathml")
	def rawStr = column[String]("rawstr")
	def showFunction = column[Boolean]("show_function")
	def derivativeOrder = column[DerivativeOrder]("derivativeOrder")

	def * = (id, ownerId, mathML, rawStr, showFunction, creationDate, derivativeOrder, atCreationDifficulty, order) <> (DerivativeGraphQuestion.tupled, DerivativeGraphQuestion.unapply _)

	def idFK = foreignKey("derivative_graph_questions__id_fk", id, QuestionIdNext.questionIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("derivative_graph_questions__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}

class DerivativeGraphQuestion2QuizTable(tag: Tag) extends Table[Question2Quiz](tag, "derivative_graph_question_2_quiz") with Question2QuizTable {

	def * = (questionId, quizId, ownerId, creationDate, order) <> (Question2Quiz.tupled, Question2Quiz.unapply _)

	def questionFK = foreignKey("derivative_graph_question_2_quiz__question_fk", questionId, derivativeGraphQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("derivative_graph_question_2_quiz__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIndex = index("derivative_graph_question_2_quiz__question_index", questionId)
	def quizIndex = index("derivative_graph_question_2_quiz__quiz_index", quizId)
}