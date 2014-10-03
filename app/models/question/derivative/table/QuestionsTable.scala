package models.question.derivative.table

import com.artclod.mathml.scalar._
import play.api.db.slick.Config.driver.simple._
import models.question.derivative.table.MathMLMapper._
import models.question.derivative._
import models.support._
import org.joda.time.DateTime
import com.artclod.slick.JodaUTC._
import service.table.UsersTable
import scala.slick.model.ForeignKeyAction

class QuestionsTable(tag: Tag) extends Table[Question](tag, "derivative_questions") {
	def id = column[QuestionId]("id", O.PrimaryKey, O.AutoInc)
	def ownerId = column[UserId]("owner")
	def mathML = column[MathMLElem]("mathml")
	def rawStr = column[String]("rawstr")
	def creationDate = column[DateTime]("creation_date")

	def * = (id, ownerId, mathML, rawStr, creationDate) <> (Question.tupled, Question.unapply _)

	def ownerFK = foreignKey("derivative_questions_owner_fk", ownerId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
