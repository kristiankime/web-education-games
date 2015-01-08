package models.quiz

sealed abstract class Status

object Status{
	def apply(attempted: Boolean, correct: Boolean) = (attempted, correct) match {
		case (true, true) => Correct
		case (true, false) => Incorrect
		case (false,false) => Unanswered
		case (false, true) => throw new IllegalArgumentException("Cannot have a correct answer with no attempts")
	}
}

object Correct extends Status {
  override def toString = "Correct"
}

object Incorrect extends Status {
  override def toString = "Incorrect"
}

object Unanswered extends Status {
  override def toString = "Unanswered"
}