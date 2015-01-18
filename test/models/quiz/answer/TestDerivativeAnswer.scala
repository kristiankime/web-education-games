package models.quiz.answer

import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC
import models.quiz.Correct2Short
import models.support._
import org.joda.time.DateTime

object TestDerivativeAnswer {
	def apply(owner: UserId,
		questionId: QuestionId,
		mathML: MathMLElem = `7`,
		rawStr: String = "7",
		correct: Boolean = false,
		creationDate: DateTime = JodaUTC.zero) =
		DerivativeAnswer(null, owner, questionId, mathML, rawStr, Correct2Short(correct), creationDate)
}