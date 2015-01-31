package models.quiz.table

import com.artclod.slick.JodaUTC._
import models.quiz.Quiz
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class QuizzesTable(tag: Tag) extends Table[Quiz](tag, "derivative_quizzes") {
	def id = column[QuizId]("id", O.PrimaryKey, O.AutoInc)
	def ownerId = column[UserId]("owner")
	def name = column[String]("name")
	def creationDate = column[DateTime]("creation_date")
	def updateDate = column[DateTime]("update_date")
	def * = (id, ownerId, name, creationDate, updateDate) <> (Quiz.tupled, Quiz.unapply _)

	def ownerFK = foreignKey("derivative_quizzes__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
