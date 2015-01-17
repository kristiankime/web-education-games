package models.quiz

import com.artclod.slick.JodaUTC
import models.support.UserId
import org.joda.time.DateTime

object TestQuiz {
	def apply(owner: UserId,
		name: String = "quiz",
		date: DateTime = JodaUTC.zero) = Quiz(null, owner, name, date, date)
}