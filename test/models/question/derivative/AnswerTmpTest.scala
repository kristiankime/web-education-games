package models.question.derivative

import models.organization.SectionTmp
import models.id.CourseId
import models.id.UserId
import org.joda.time.DateTime
import models.id.QuestionId
import mathml.scalar.MathMLElem

object AnswerTmpTest {
	def apply(owner: UserId,
		questionId: QuestionId,
		mathML: MathMLElem = mathml.scalar.`7`,
		rawStr: String = "7",
		correct: Boolean = false,
		creationDate: DateTime = DateTime.now) =
		AnswerTmp(owner, questionId, mathML, rawStr, correct, creationDate)
}