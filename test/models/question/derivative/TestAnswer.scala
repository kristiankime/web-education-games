package models.question.derivative

import models.support._
import org.joda.time.DateTime
import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC


object TestAnswer {
	def apply(owner: UserId,
		questionId: QuestionId,
		mathML: MathMLElem = `7`,
		rawStr: String = "7",
		correct: Boolean = false,
		creationDate: DateTime = JodaUTC.zero) =
		Answer(null, owner, questionId, mathML, rawStr, correct, creationDate)
}