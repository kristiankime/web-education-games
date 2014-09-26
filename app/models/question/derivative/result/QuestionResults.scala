package models.question.derivative.result

import models.question.Status
import models.question.derivative.{Answer, Question}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.Session
import service.User
import viewsupport.question.derivative.{Correct, Ongoing, Unstarted}

case class QuestionResults(answerer: User, question: Question, answers: List[Answer], startTime: Option[DateTime]) {
  require(answers.forall(_.ownerId == answerer.id), "All the answers must be from the same user")

  def viewed = startTime.nonEmpty

  def attempted = answers.nonEmpty

  def numAttempts = answers.size

  val correct = answers.foldLeft(false)(_ || _.correct)

  val status = Status(attempted, correct)

  val firstCorrect = answers.find(_.correct)

  val timeToCorrect = (startTime, firstCorrect) match {
    case (None, _) => Unstarted
    case (Some(start), None) => Ongoing(start)
    case (Some(start), Some(first)) => Correct(start, first.creationDate)
  }

  val numAttemptsToCorrect = {
    val num = answers.indexWhere(_.correct)
    if (num == -1) None else Some(num + 1)
  }

  val score: Option[Double] =
    if (!attempted) None
    else if(correct) Some(1d)
    else Some(0d)
//  else {
//    numAttemptsToCorrect match {
//      case None => Some(0.0) // No correct answers is a 0
//      case Some(1) => Some(1.0)
//      case Some(2) => Some(1.0) // First error free
//      case Some(3) => Some(0.8) // all others -.2
//      case Some(4) => Some(0.6)
//      case Some(5) => Some(0.4)
//      case Some(_) => Some(0.2) // Always get at least .2 if you get it right
//    }
//  }

  def teacherScore(implicit session: Session): Option[Double] = Some(0) // question.difficulty.map(d => if (correct) d else 1.0 - d)
}
