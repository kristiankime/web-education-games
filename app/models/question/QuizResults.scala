package models.question

import com.artclod.collection.PimpedSeq
import models.question.derivative.DerivativeQuestion
import service.User

case class QuizResults(student: User, quiz: Quiz, results: List[QuestionResults]) {
  require(results.forall(_.answerer == student), "All the question results must be from the same user")

  def numQuestions = results.size

  val numCorrect = results.map(s => if (s.correct) 1 else 0).sum

  val numAttempted = results.map(s => if (s.attempted) 1 else 0).sum

  val questions = results.map(_.question)

  def previousQuestion(question: DerivativeQuestion) = questions.elementBefore(question)

  def nextQuestion(question: DerivativeQuestion) = questions.elementAfter(question)

  def firstUnfinishedQuestion = results.find(!_.correct)

  val score = {
    val scores = results.flatMap(_.score)
    if (scores.isEmpty) None
    else Some(scores.sum / results.size)
  }

  val studentScore = results.map(_.studentScore).sum / results.size

  def teacherScore(studentSkill: Double) = results.map(_.teacherScore(studentSkill)).sum / results.size

}
