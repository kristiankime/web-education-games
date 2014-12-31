package models.question.derivative.table

import com.artclod.mathml.scalar._
import com.artclod.mathml.slick.MathMLMapper
import play.api.db.slick.Config.driver.simple._
import MathMLMapper._
import models.question.derivative._
import models.support._
import org.joda.time.DateTime
import com.artclod.slick.JodaUTC._
import service.table.UsersTable
import scala.slick.model.ForeignKeyAction
import models.question.table.{QuestionIdNext, quizzesTable}

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
