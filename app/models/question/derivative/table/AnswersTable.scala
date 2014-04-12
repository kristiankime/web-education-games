package models.question.derivative.table

import com.artclod.mathml._
import com.artclod.mathml.scalar._
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import models.question.derivative.table.MathMLMapper._
import models.question.derivative._
import models.support._
import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._
import service.table.UserTable

class AnswersTable extends Table[Answer]("derivative_answers") {
	def id = column[AnswerId]("id", O.PrimaryKey, O.AutoInc)
	def questionId = column[QuestionId]("question_id", O.NotNull)
	def owner = column[UserId]("owner", O.NotNull)
	def mathML = column[MathMLElem]("mathml", O.NotNull)
	def rawStr = column[String]("rawstr", O.NotNull)
	def correct = column[Boolean]("correct", O.NotNull)
	def creationDate = column[DateTime]("creationDate", O.NotNull)

	def * = id ~ owner ~ questionId ~ mathML ~ rawStr ~ correct ~ creationDate <> (Answer, Answer.unapply _)

	def ownerFK = foreignKey("derivative_answers_owner_fk", owner, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionFK = foreignKey("derivative_answers_fk", questionId, new QuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def autoInc = questionId ~ owner ~ mathML ~ rawStr ~ correct ~ creationDate returning id

	def insert(t: AnswerTmp)(implicit s: Session) = autoInc.insert(t.questionId, t.owner, t.mathML, t.rawStr, t.correct, t.creationDate)
}
