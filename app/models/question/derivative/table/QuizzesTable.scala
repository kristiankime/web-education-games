package models.question.derivative.table

import com.artclod.mathml._
import com.artclod.mathml.scalar._
import play.api.db.slick.Config.driver.simple._
import models.question.derivative.table.MathMLMapper._
import models.question.derivative._
import models.support._
import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._
import service.table.UserTable
import scala.slick.lifted.ForeignKeyAction

class QuizzesTable extends Table[Quiz]("derivative_quizzes") {
	def id = column[QuizId]("id", O.PrimaryKey, O.AutoInc)
	def owner = column[UserId]("owner", O.NotNull)
	def name = column[String]("name", O.NotNull)
	def creationDate = column[DateTime]("creationDate")
	def updateDate = column[DateTime]("upadateDate")
	def * = id ~ owner ~ name ~ creationDate ~ updateDate <> (Quiz, Quiz.unapply _)

	def ownerFK = foreignKey("derivative_quizzes_owner_fk", owner, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	
	def autoInc = owner ~ name ~ creationDate ~ updateDate returning id
	
	def insert(t: QuizTmp)(implicit s: Session) = this.autoInc.insert(t.owner, t.name, t.date, t.date)
}
