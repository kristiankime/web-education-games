package models.question.derivative.table

import models.question.Quiz
import play.api.db.slick.Config.driver.simple._
import models.question.derivative._
import models.support._
import org.joda.time.DateTime
import service.table.UsersTable
import com.artclod.slick.JodaUTC._
import scala.slick.model.ForeignKeyAction

class QuizzesTable(tag: Tag) extends Table[Quiz](tag, "derivative_quizzes") {
	def id = column[QuizId]("id", O.PrimaryKey, O.AutoInc)
	def ownerId = column[UserId]("owner")
	def name = column[String]("name")
	def creationDate = column[DateTime]("creation_date")
	def updateDate = column[DateTime]("update_date")
	def * = (id, ownerId, name, creationDate, updateDate) <> (Quiz.tupled, Quiz.unapply _)

	def ownerFK = foreignKey("derivative_quizzes_owner_fk", ownerId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
