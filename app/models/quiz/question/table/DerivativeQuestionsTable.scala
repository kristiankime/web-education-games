package models.quiz.question.table

import com.artclod.mathml.scalar._
import com.artclod.mathml.slick.MathMLMapper._
import com.artclod.slick.JodaUTC._
import models.quiz.question.DerivativeQuestion
import models.quiz.table.{QuestionIdNext, quizzesTable}
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.UsersTable

import scala.slick.model.ForeignKeyAction

class DerivativeQuestionsTable(tag: Tag) extends Table[DerivativeQuestion](tag, "derivative_questions") {
	def id = column[QuestionId]("id", O.PrimaryKey)
	def ownerId = column[UserId]("owner")
	def mathML = column[MathMLElem]("mathml")
	def rawStr = column[String]("rawstr")
	def creationDate = column[DateTime]("creation_date")
	def quizId = column[Option[QuizId]]("quiz_id")
	def order = column[Int]("order")

	def * = (id, ownerId, mathML, rawStr, creationDate, quizId, order) <> (DerivativeQuestion.tupled, DerivativeQuestion.unapply _)

	def idFK = foreignKey("derivative_questions__id_fk", id, QuestionIdNext.questionIdTable)(_.questionId, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("derivative_questions__owner_fk", ownerId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("derivative_questions__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)

}