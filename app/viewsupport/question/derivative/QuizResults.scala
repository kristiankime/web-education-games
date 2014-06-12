package viewsupport.question.derivative

import org.joda.time.{Duration, DateTime}
import com.artclod.collection.PimpedSeq
import play.api.db.slick.Config.driver.simple._
import models.question.Status
import models.question.derivative._
import service.User


case class QuizResults(quiz: Quiz, results: List[UserQuizResults])

case class UserQuizResults(user: User, quiz: Quiz, results: List[QuestionResults]) {
  def numQuestions = results.size

  val numCorrect = results.map(s => if (s.correct) 1 else 0).sum

  val numAttempted = results.map(s => if (s.attempted) 1 else 0).sum

  val questions = results.map(_.question)

  def previousQuestion(question: Question) = questions.elementBefore(question)

  def nextQuestion(question: Question) = questions.elementAfter(question)

  val score = {
    val scores = results.flatMap(_.score)
    if (scores.isEmpty) None
    else Some(scores.sum / scores.size)
  }

  def teacherScore(implicit session: Session): Option[Double] = {
    val teacherScores = results.flatMap(_.teacherScore)
    if (teacherScores.isEmpty) None
    else Some(teacherScores.sum / teacherScores.size)
  }
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

  val score: Option[Double] = if (!attempted) None
  else {
    numAttemptsToCorrect match {
      case None => Some(0.0) // No correct answers is a 0
      case Some(1) => Some(1.0)
      case Some(2) => Some(1.0) // First error free
      case Some(3) => Some(0.8) // all others -.2
      case Some(4) => Some(0.6)
      case Some(5) => Some(0.4)
      case Some(_) => Some(0.2) // Always get at least .2 if you get it right
    }
  }

  def teacherScore(implicit session: Session): Option[Double] = question.difficulty.map(d => if (correct) d else 1.0 - d)
}

sealed trait AnswerTime

object Unstarted extends AnswerTime

case class Ongoing(start: DateTime) extends AnswerTime

case class Correct(start: DateTime, correct: DateTime) extends AnswerTime {
  val time = new Duration(start, correct)
}

