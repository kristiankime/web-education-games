package models.question.derivative

import models.organization.SectionTmp
import models.support.CourseId
import models.support.UserId
import org.joda.time.DateTime
import models.support.QuestionId
import mathml.scalar.MathMLElem

object QuizTmpTest {
	def apply(owner: UserId,
		name: String = "quiz",
		date: DateTime = DateTime.now) =
		QuizTmp(owner, name, date)
}