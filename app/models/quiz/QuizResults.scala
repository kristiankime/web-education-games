package models.quiz

import com.artclod.collection.PimpedSeq
import models.quiz.answer.result.QuestionResults
import models.quiz.question.Question
import models.user.User

case class QuizResults(student: User, quiz: Quiz, results: List[QuestionResults]) {
  require(results.forall(_.answerer.id == student.id), "All the question results must be from the same user")

  def numQuestions = results.size

  val numCorrect = results.map(s => if (s.correct) 1 else 0).sum

  val numAttempted = results.map(s => if (s.attempted) 1 else 0).sum

  val questions = results.map(_.question)

  def previousQuestion(question: Question) = questions.elementBefore(question)

  def nextQuestion(question: Question) = questions.elementAfter(question)

  def firstUnfinishedQuestion = results.find(!_.correct)

  val score = {
    val scores = results.flatMap(_.score)
    if (scores.isEmpty) None
    else Some(scores.sum / results.size)
  }

  val studentScore = results.map(_.studentScore).sum / results.size

  def teacherScore(studentSkill: Double) = results.map(_.teacherScore(studentSkill)).sum / results.size

}
