package models.question.derivative.table

import com.artclod.slick.NumericBoolean.boolean2DBNumber
import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC._
import models.question.derivative.table.MathMLMapper._
import models.question.derivative._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.{booleanColumnType => _, _}
import scala.slick.model.ForeignKeyAction
import service.table.UsersTable

class AnswersTable(tag: Tag) extends Table[Answer](tag, "derivative_answers") {
	def id = column[AnswerId]("id", O.PrimaryKey, O.AutoInc)
	def questionId = column[QuestionId]("question_id")
	def ownerId = column[UserId]("owner")
	def mathML = column[MathMLElem]("mathml")
	def rawStr = column[String]("rawstr")
	def correct = column[Boolean]("correct") // Note the import com.artclod.slick.NumericBoolean.boolean2DBNumber this is represented in the DB as a number
	def creationDate = column[DateTime]("creation_date")

	def * = (id, ownerId, questionId, mathML, rawStr, correct, creationDate) <> (Answer.tupled, Answer.unapply _)

	def ownerFK = foreignKey("derivative_answers_owner_fk", ownerId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionFK = foreignKey("derivative_answers_fk", questionId, questionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
