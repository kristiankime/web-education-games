package models.quiz.question.table

import com.artclod.mathml.scalar._
import com.artclod.mathml.slick.MathMLMapper._
import com.artclod.slick.JodaUTC._
import models.quiz.question.DerivativeQuestion
import models.quiz.table.{QuestionIdNext, quizzesTable}
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable
import com.artclod.slick.JodaUTC._

import scala.slick.model.ForeignKeyAction

class DerivativeQuestionsTable(tag: Tag) extends Table[DerivativeQuestion](tag, "derivative_questions") with QuestionsTable[DerivativeQuestion] {
	def mathML = column[MathMLElem]("mathml")
	def rawStr = column[String]("rawstr")

	def * = (id, ownerId, mathML, rawStr, creationDate, atCreationDifficulty, quizId, order) <> (DerivativeQuestion.tupled, DerivativeQuestion.unapply _)

	def idFK = foreignKey("derivative_questions__id_fk", id, QuestionIdNext.questionIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("derivative_questions__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("derivative_questions__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
