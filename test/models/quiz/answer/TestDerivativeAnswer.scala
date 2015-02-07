package models.quiz.answer

import com.artclod.mathml.scalar._
import com.artclod.slick.{NumericBoolean, JodaUTC}
import models.support._
import org.joda.time.DateTime

object TestDerivativeAnswer {
	def apply(owner: UserId,
		questionId: QuestionId,
		mathML: MathMLElem = `7`,
		rawStr: String = "7",
		correct: Boolean = false,
		creationDate: DateTime = JodaUTC.zero) =
		DerivativeAnswer(null, owner, questionId, mathML, rawStr, NumericBoolean(correct), creationDate)
}