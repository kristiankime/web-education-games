package models.question.derivative.table

import com.artclod.mathml.scalar._
import play.api.db.slick.Config.driver.simple._
import models.question.derivative.table.MathMLMapper._
import models.question.derivative._
import models.support._
import org.joda.time.DateTime
import com.artclod.slick.Joda._
import service.table.UserTable
import scala.slick.model.ForeignKeyAction

class QuestionsTable(tag: Tag) extends Table[Question](tag, "derivative_questions") {
	def id = column[QuestionId]("id", O.PrimaryKey, O.AutoInc)
	def owner = column[UserId]("owner", O.NotNull)
	def mathML = column[MathMLElem]("mathml", O.NotNull)
	def rawStr = column[String]("rawstr", O.NotNull)
	def creationDate = column[DateTime]("creationDate")

	def * = (id, owner, mathML, rawStr, creationDate) <> (Question.tupled, Question.unapply _)

	def ownerFK = foreignKey("derivative_questions_owner_fk", owner, UserTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	
//	def autoInc = owner ~ mathML ~ rawStr ~ creationDate returning id
//
//	def insert(t: QuestionTmp)(implicit s: Session) = this.autoInc.insert(t.owner, t.mathML, t.rawStr, t.creationDate)
}
