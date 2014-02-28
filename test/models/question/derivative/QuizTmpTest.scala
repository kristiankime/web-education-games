package models.question.derivative

import models.organization.SectionTmp
import models.id.CourseId
import models.id.UserId
import org.joda.time.DateTime
import models.id.QuestionId
import mathml.scalar.MathMLElem

object QuizTmpTest {
	def apply(owner: UserId,
		name: String = "quiz",
		date: DateTime = DateTime.now) =
		QuizTmp(owner, name, date)
}