package models.question.derivative

import models.question.Quiz
import models.support.UserId
import org.joda.time.DateTime
import com.artclod.slick.JodaUTC

object TestQuiz {
	def apply(owner: UserId,
		name: String = "quiz",
		date: DateTime = JodaUTC.zero) = Quiz(null, owner, name, date, date)
}