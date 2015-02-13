package models.quiz.question.table

import com.artclod.mathml.scalar._
import com.artclod.mathml.slick.MathMLMapper._
import com.artclod.slick.JodaUTC._
import models.quiz.question.TangentQuestion
import models.quiz.table.{QuestionIdNext, quizzesTable}
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class TangentQuestionsTable(tag: Tag) extends Table[TangentQuestion](tag, "tangent_questions") with QuestionsTable[TangentQuestion] {
	def function = column[MathMLElem]("function")
	def functionStr = column[String]("function_str")
	def atPointX = column[MathMLElem]("at_point_x")
	def atPointXStr = column[String]("at_point_x_str")

	def * = (id, ownerId, function, functionStr, atPointX, atPointXStr, creationDate, atCreationDifficulty, quizId, order) <> (TangentQuestion.tupled, TangentQuestion.unapply _)

	def idFK = foreignKey("tangent_questions__id_fk", id, QuestionIdNext.questionIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("tangent_questions__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("tangent_questions__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
