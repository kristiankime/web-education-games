package models.question.derivative.table

import org.joda.time.DateTime
import com.artclod.mathml.scalar._
import com.github.tototoshi.slick.JodaSupport._
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import models.question.derivative.table.MathMLMapper._
import models.question.derivative._
import models.support._
import service.table.UserTable

class AnswersTable extends Table[Answer]("derivative_answers") {
	def id = column[AnswerId]("id", O.PrimaryKey, O.AutoInc)
	def questionId = column[QuestionId]("question_id", O.NotNull)
	def owner = column[UserId]("owner", O.NotNull)
	def mathML = column[MathMLElem]("mathml", O.NotNull)
	def rawStr = column[String]("rawstr", O.NotNull)
	def correct = column[Boolean]("correct", O.NotNull)
	def creationDate = column[DateTime]("creation_date", O.NotNull)

  def columnsNoId = owner ~ questionId ~ mathML ~ rawStr ~ correct ~ creationDate
	def * = id ~: columnsNoId <> (Answer, Answer.unapply _)

	def ownerFK = foreignKey("derivative_answers_owner_fk", owner, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionFK = foreignKey("derivative_answers_fk", questionId, new QuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def autoInc = columnsNoId returning id

	def insert(t: AnswerTmp)(implicit s: Session) = autoInc.insert(AnswerTmp.unapply(t).get)
}
