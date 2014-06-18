package models.question.derivative

import models.support.UserId
import org.joda.time.DateTime
import com.artclod.slick.Joda

object TestQuiz {
	def apply(owner: UserId,
		name: String = "quiz",
		date: DateTime = Joda.zero) = Quiz(null, owner, name, date, date)
}