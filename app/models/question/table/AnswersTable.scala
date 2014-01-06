package models.question.table

import mathml._
import mathml.scalar._
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import models.question.table.MathMLMapper._
import models.question.Answer

object AnswersTable extends Table[Answer]("derivative_answers") {
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def questionId = column[Long]("question_id", O.NotNull)
	def mathML = column[MathMLElem]("mathml", O.NotNull)
	def rawStr = column[String]("rawstr", O.NotNull)
	def synched = column[Boolean]("synched", O.NotNull)
	def correct = column[Boolean]("correct", O.NotNull)
	def * = id ~ questionId ~ mathML ~ rawStr ~ synched ~ correct <> (Answer, Answer.unapply _)

	def autoInc = questionId ~ mathML ~ rawStr ~ synched ~ correct returning id

	def questionFK = foreignKey("derivative_answers_fk", questionId, QuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
