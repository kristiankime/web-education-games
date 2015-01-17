package models.quiz.question

import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC
import models.support.UserId
import org.joda.time.DateTime

object TestDerivativeQuestion {
	def apply(owner: UserId,
		mathML: MathMLElem = `6`,
		rawStr: String = "6",
		creationDate: DateTime = JodaUTC.zero) =
		DerivativeQuestion(null, owner, mathML, rawStr, creationDate, 0d)
}