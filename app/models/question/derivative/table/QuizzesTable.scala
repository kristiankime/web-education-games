package models.question.derivative.table

import mathml._
import mathml.scalar._
import play.api.db.slick.Config.driver.simple._
import models.question.derivative.table.MathMLMapper._
import models.question.derivative._
import models.id._
import models.id.Ids._

object QuizzesTable extends Table[Quiz]("derivative_quizzes") {
	def id = column[QuizId]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def * = id ~ name <> (Quiz, Quiz.unapply _)

	def autoInc = name returning id
}
