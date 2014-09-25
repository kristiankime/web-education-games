package models.question.derivative.result

import models.question.derivative.{Question, Quiz}
import com.artclod.collection.PimpedSeq

trait QuizResults {
  val quiz: Quiz
  val results: List[QuestionResults]

  def numQuestions = results.size

  val numCorrect = results.map(s => if (s.correct) 1 else 0).sum

  val numAttempted = results.map(s => if (s.attempted) 1 else 0).sum

  val questions = results.map(_.question)

  def previousQuestion(question: Question) = questions.elementBefore(question)

  def nextQuestion(question: Question) = questions.elementAfter(question)

  def firstUnfinishedQuestion = results.find(!_.correct)
}
