package models.question.derivative.result

import models.question.{QuestionScore, Status}
import models.question.derivative.{DerivativeAnswer, DerivativeQuestion}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.Session
import service.User
import viewsupport.question.derivative.{Correct, Ongoing, Unstarted}

case class QuestionResults(answerer: User, question: DerivativeQuestion, answers: List[DerivativeAnswer]) {
  require(answers.forall(_.ownerId == answerer.id), "All the answers must be from the same user")

  def attempted = answers.nonEmpty

  def numAttempts = answers.size

  val correct = answers.foldLeft(false)(_ || _.correct)

  val status = Status(attempted, correct)

  val firstCorrect = answers.find(_.correct)

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
