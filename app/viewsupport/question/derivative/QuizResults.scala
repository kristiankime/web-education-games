package viewsupport.question.derivative

import com.artclod.collection.PimpedSeq
import play.api.db.slick.Config.driver.simple._
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





