package models.question.derivative.result

import models.question.Status
import models.question.derivative.{QuestionScore, Answer, Question}
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

  def studentScore = if(correct) 1d else 0d

  def teacherScore(studentSkill: Double): Double = QuestionScore.teacherScore(question, correct, studentSkill)
}
