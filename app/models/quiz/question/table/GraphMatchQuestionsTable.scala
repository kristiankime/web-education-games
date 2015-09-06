package models.quiz.question.table

import com.artclod.mathml.scalar._
import com.artclod.mathml.slick.MathMLMapper._
import models.quiz.question.{GraphMatchQuestion, DerivativeGraphQuestion}
import models.quiz.question.support.DerivativeOrder
import models.quiz.question.support.DerivativeOrder.string2DerivativeOrder
import models.quiz.table.{QuestionIdNext, quizzesTable}
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class GraphMatchQuestionsTable(tag: Tag) extends Table[GraphMatchQuestion](tag, "graph_match_questions") with QuestionsTable[GraphMatchQuestion] {
	def function1Math = column[MathMLElem]("function1Math")
	def function1Raw  = column[String]("function1Raw")
	def function2Math = column[MathMLElem]("function2Math")
	def function2Raw  = column[String]("function2Raw")
	def function3Math = column[MathMLElem]("function3Math")
	def function3Raw  = column[String]("function3Raw")
	def graphThis     = column[Short]("graphThis")

	def * = (id, ownerId, function1Math, function1Raw, function2Math, function2Raw, function3Math, function3Raw, graphThis, creationDate, atCreationDifficulty, quizId, order) <> (GraphMatchQuestion.tupled, GraphMatchQuestion.unapply _)

	def idFK = foreignKey("graph_match_questions__id_fk", id, QuestionIdNext.questionIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("graph_match_questions__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("graph_match_questions__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
