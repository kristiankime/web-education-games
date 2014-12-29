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

class DerivativeQuestionsTable(tag: Tag) extends Table[DerivativeQuestion](tag, "derivative_questions") {
	def id = column[QuestionId]("id", O.PrimaryKey, O.AutoInc)
	def ownerId = column[UserId]("owner")
	def mathML = column[MathMLElem]("mathml")
	def rawStr = column[String]("rawstr")
	def creationDate = column[DateTime]("creation_date")

	def * = (id, ownerId, mathML, rawStr, creationDate) <> (DerivativeQuestion.tupled, DerivativeQuestion.unapply _)

	def ownerFK = foreignKey("derivative_questions_owner_fk", ownerId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}