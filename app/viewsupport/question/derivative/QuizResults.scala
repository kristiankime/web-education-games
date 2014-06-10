package viewsupport.question.derivative

import service.User
import com.artclod.collection.PimpedSeq
import models.question.Status
import models.question.derivative._
import org.joda.time.{Duration, DateTime}

case class QuizResults(quiz: Quiz, results: List[UserQuizResults])

case class UserQuizResults(user: User, quiz: Quiz, results: List[QuestionResults]) {
//  require(results.forall(_.answerer == user.id), "All the question results must be from the same user")

  def numQuestions = results.size

  val numCorrect = results.map(s => if (s.correct) 1 else 0).sum

  val numAttempted = results.map(s => if (s.attempted) 1 else 0).sum

  val questions = results.map(_.question)

  def previousQuestion(question: Question) = questions.elementBefore(question)

  def nextQuestion(question: Question) = questions.elementAfter(question)

  val score = results.map(_.score).foldLeft(Option(0d))((a, b) => (a, b) match {
    case (Some(v1), Some(v2)) => Some(v1 + v2)
    case _ => None
  }).map( _ / numQuestions)
}

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

  val score: Option[Double] = numAttemptsToCorrect.map(_ match {
    case 1 => 1.0
    case 2 => 1.0 // First error free
    case 3 => 0.8 // all others -.2
    case 4 => 0.6
    case 5 => 0.4
    case _ => 0.2 // Always get at least .2
  })
}

sealed trait AnswerTime

object Unstarted extends AnswerTime

case class Ongoing(start: DateTime) extends AnswerTime

case class Correct(start: DateTime, correct: DateTime) extends AnswerTime {
  val time = new Duration(start, correct)
}

