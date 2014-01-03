package models.question.table

import mathml._
import mathml.scalar._
import play.api.db.slick.Config.driver.simple._
import models.mapper.MathMLMapper._
import models.question.DerivativeQuestion

object DerivativeQuestionsTable extends Table[DerivativeQuestion]("derivative_questions") {
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def mathML = column[MathMLElem]("mathml", O.NotNull)
	def rawStr = column[String]("rawstr", O.NotNull)
	def synched = column[Boolean]("synched", O.NotNull)
	def * = id ~ mathML ~ rawStr ~ synched <> (DerivativeQuestion, DerivativeQuestion.unapply _)
	
	def autoInc = mathML ~ rawStr ~ synched returning id
}

