package viewsupport.question.derivative

import org.joda.time.{Duration, DateTime}

sealed trait AnswerTime

object Unstarted extends AnswerTime

case class Ongoing(start: DateTime) extends AnswerTime

case class Correct(start: DateTime, correct: DateTime) extends AnswerTime {
  val time = new Duration(start, correct)
}