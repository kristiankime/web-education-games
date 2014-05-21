package models.question.derivative.table

import play.api.db.slick.Config.driver.simple._
import models.question.derivative._
import models.support._
import org.joda.time.DateTime
import service.table.UserTable
import com.artclod.slick.Joda._
import scala.slick.model.ForeignKeyAction

class QuizzesTable(tag: Tag) extends Table[Quiz](tag, "derivative_quizzes") {
	def id = column[QuizId]("id", O.PrimaryKey, O.AutoInc)
	def owner = column[UserId]("owner", O.NotNull)
	def name = column[String]("name", O.NotNull)
	def creationDate = column[DateTime]("creationDate")
	def updateDate = column[DateTime]("upadateDate")
	def * = (id, owner, name, creationDate, updateDate) <> (Quiz.tupled, Quiz.unapply _)

	def ownerFK = foreignKey("derivative_quizzes_owner_fk", owner, UserTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	
//	def autoInc = owner ~ name ~ creationDate ~ updateDate returning id
//
//	def insert(t: QuizTmp)(implicit s: Session) = this.autoInc.insert(t.owner, t.name, t.date, t.date)
}
