package models.quiz.derivative

import models.quiz.question.DerivativeQuestion
import models.support.UserId
import org.joda.time.DateTime
import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC

object TestQuestion {
	def apply(owner: UserId,
		mathML: MathMLElem = `6`,
		rawStr: String = "6",
		creationDate: DateTime = JodaUTC.zero) =
		DerivativeQuestion(null, owner, mathML, rawStr, creationDate)
}