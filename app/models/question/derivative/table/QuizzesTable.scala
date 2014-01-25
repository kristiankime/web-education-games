package models.question.derivative.table

import mathml._
import mathml.scalar._
import play.api.db.slick.Config.driver.simple._
import models.question.derivative.table.MathMLMapper._
import models.question.derivative._
import models.id._
import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._

class QuizzesTable extends Table[Quiz]("derivative_quizzes") {
	def id = column[QuizId]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def creationDate = column[DateTime]("creationDate")
	def updateDate = column[DateTime]("upadateDate")
	def * = id ~ name ~ creationDate ~ updateDate <> (Quiz, Quiz.unapply _)

	def autoInc = name ~ creationDate ~ updateDate returning id
	
	def insert(t: QuizTmp)(implicit s: Session) = this.autoInc.insert(t.name, t.date, t.date)
}
