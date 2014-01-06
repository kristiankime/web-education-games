package models.question.table

import mathml._
import mathml.scalar._
import play.api.db.slick.Config.driver.simple._
import models.question.table.MathMLMapper._
import models.question.Question

object QuestionsTable extends Table[Question]("derivative_questions") {
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def mathML = column[MathMLElem]("mathml", O.NotNull)
	def rawStr = column[String]("rawstr", O.NotNull)
	def synched = column[Boolean]("synched", O.NotNull)
	def * = id ~ mathML ~ rawStr ~ synched <> (Question, Question.unapply _)
	
	def autoInc = mathML ~ rawStr ~ synched returning id
}

