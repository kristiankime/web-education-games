package models.question.derivative.table

import org.joda.time.DateTime
import com.artclod.mathml.scalar._
import com.artclod.slick.Joda._
import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import models.question.derivative.table.MathMLMapper._
import models.question.derivative._
import models.support._
import service.table.UsersTable

class AnswersTable(tag: Tag) extends Table[Answer](tag, "derivative_answers") {
	def id = column[AnswerId]("id", O.PrimaryKey, O.AutoInc)
	def questionId = column[QuestionId]("question_id", O.NotNull)
	def ownerId = column[UserId]("owner", O.NotNull)
	def mathML = column[MathMLElem]("mathml", O.NotNull)
	def rawStr = column[String]("rawstr", O.NotNull)
	def correct = column[Boolean]("correct", O.NotNull)
	def creationDate = column[DateTime]("creation_date", O.NotNull)

	def * = (id, ownerId, questionId, mathML, rawStr, correct, creationDate) <> (Answer.tupled, Answer.unapply _)

	def ownerFK = foreignKey("derivative_answers_owner_fk", ownerId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionFK = foreignKey("derivative_answers_fk", questionId, questionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
