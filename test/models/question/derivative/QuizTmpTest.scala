package models.question.derivative

import models.support.CourseId
import models.support.UserId
import org.joda.time.DateTime
import models.support.QuestionId
import com.artclod.mathml.scalar.MathMLElem

object QuizTmpTest {
	def apply(owner: UserId,
		name: String = "quiz",
		date: DateTime = DateTime.now) = Quiz(null, owner, name, date, date)
}