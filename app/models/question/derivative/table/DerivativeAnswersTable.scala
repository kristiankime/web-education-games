package models.question.derivative.table

import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC._
import models.question.derivative._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import service.table.UsersTable
import com.artclod.mathml.slick.MathMLMapper.string2mathML

class DerivativeAnswersTable(tag: Tag) extends Table[DerivativeAnswer](tag, "derivative_answers") {
	def id = column[AnswerId]("id", O.PrimaryKey, O.AutoInc)
	def questionId = column[QuestionId]("question_id")
	def ownerId = column[UserId]("owner")
	def mathML = column[MathMLElem]("mathml")
	def rawStr = column[String]("rawstr")
	def correct = column[Short]("correct") // Note this represent a Boolean in the Answers Class, kept as a number for aggregation purposes
	def creationDate = column[DateTime]("creation_date")

	def * = (id, ownerId, questionId, mathML, rawStr, correct, creationDate) <> (DerivativeAnswer.tupled, DerivativeAnswer.unapply _)

	def ownerFK = foreignKey("derivative_answers_owner_fk", ownerId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionFK = foreignKey("derivative_answers_fk", questionId, questionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}