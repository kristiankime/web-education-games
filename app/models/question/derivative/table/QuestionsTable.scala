package models.question.derivative.table

import mathml._
import mathml.scalar._
import play.api.db.slick.Config.driver.simple._
import models.question.derivative.table.MathMLMapper._
import models.question.derivative._
import models.id._
import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._
import service.table.UserTable
import scala.slick.lifted.ForeignKeyAction

class QuestionsTable extends Table[Question]("derivative_questions") {
	def id = column[QuestionId]("id", O.PrimaryKey, O.AutoInc)
	def owner = column[UserId]("owner", O.NotNull)
	def mathML = column[MathMLElem]("mathml", O.NotNull)
	def rawStr = column[String]("rawstr", O.NotNull)
	def synched = column[Boolean]("synched", O.NotNull)
	def creationDate = column[DateTime]("creationDate")

	def * = id ~ owner ~ mathML ~ rawStr ~ synched ~ creationDate <> (Question, Question.unapply _)

	def ownerFK = foreignKey("derivative_questions_owner_fk", owner, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	
	def autoInc = owner ~ mathML ~ rawStr ~ synched ~ creationDate returning id

	def insert(t: QuestionTmp)(implicit s: Session) = this.autoInc.insert(t.owner, t.mathML, t.rawStr, t.synched, t.creationDate)
}
