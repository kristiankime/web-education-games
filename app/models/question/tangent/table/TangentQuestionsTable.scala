package models.question.tangent.table

import com.artclod.mathml.scalar._
import com.artclod.mathml.slick.MathMLMapper
import com.artclod.mathml.slick.MathMLMapper._
import com.artclod.slick.JodaUTC._
import models.question.derivative._
import models.question.table.quizzesTable
import models.question.tangent.TangentQuestion
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.UsersTable

import scala.slick.model.ForeignKeyAction

class TangentQuestionsTable(tag: Tag) extends Table[TangentQuestion](tag, "tangent_questions") {
	def id = column[QuestionId]("id", O.PrimaryKey, O.AutoInc)
	def ownerId = column[UserId]("owner")
	def function = column[MathMLElem]("function")
	def functionStr = column[String]("function_str")
	def atPointX = column[MathMLElem]("at_point_x")
	def atPointXStr = column[String]("at_point_x_str")
	def creationDate = column[DateTime]("creation_date")
	def quizId = column[Option[QuizId]]("quiz_id")
	def order = column[Int]("order")

	def * = (id, ownerId, function, functionStr, atPointX, atPointXStr, creationDate, quizId, order) <> (TangentQuestion.tupled, TangentQuestion.unapply _)

	def ownerFK = foreignKey("tangent_questions__owner_fk", ownerId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("tangent_questions__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)

}
