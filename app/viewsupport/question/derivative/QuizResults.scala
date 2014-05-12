package viewsupport.question.derivative

import service.User
import com.artclod.collection.PimpedSeq
import models.question.Status
import models.question.derivative._

case class QuizResults(quiz: Quiz, results: List[UserQuizResults])

case class UserQuizResults(user: User, quiz: Quiz, results: List[QuestionResults]) {

	def numQuestions = results.size

	val numCorrect = results.map(s => if (s.correct) { 1 } else { 0 }).sum

	val numAttempted = results.map(s => if (s.attempted) { 1 } else { 0 }).sum

	val questions = results.map(_.question)

  def previousQuestion(question: Question) = questions.elementBefore(question)

  def nextQuestion(question: Question) = questions.elementAfter(question)

}

case class QuestionResults(question: Question, answers: List[Answer]) {

	val correct = answers.foldLeft(false)(_ || _.correct)

	def attempted = answers.nonEmpty

	val status = Status(attempted, correct)
}



