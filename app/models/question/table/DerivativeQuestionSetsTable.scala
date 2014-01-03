package models.question.table

import mathml._
import mathml.scalar._
import play.api.db.slick.Config.driver.simple._
import models.question.DerivativeQuestionSet

object DerivativeQuestionSetsTable extends Table[DerivativeQuestionSet]("derivative_question_sets") {
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def * = id ~ name <> (DerivativeQuestionSet, DerivativeQuestionSet.unapply _)

	def autoInc = name returning id
}
