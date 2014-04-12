package models.question.derivative

import models.organization.SectionTmp
import models.support.CourseId
import models.support.UserId
import org.joda.time.DateTime
import models.support.QuestionId
import com.artclod.mathml.scalar.MathMLElem

object AnswerTmpTest {
	def apply(owner: UserId,
		questionId: QuestionId,
		mathML: MathMLElem = mathml.scalar.`7`,
		rawStr: String = "7",
		correct: Boolean = false,
		creationDate: DateTime = DateTime.now) =
		AnswerTmp(owner, questionId, mathML, rawStr, correct, creationDate)
}