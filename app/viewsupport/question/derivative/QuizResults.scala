package viewsupport.question.derivative

import service.User
import com.artclod.collection.PimpedSeq
import models.question.Status

case class QuizResults(quiz: Quiz, results: List[UserQuizResults])

case class UserQuizResults(user: User, quiz: Quiz, results: List[QuestionResults]) {

	def numQuestions = results.size

	val numCorrect = results.map(s => if (s.correct) { 1 } else { 0 }).foldLeft(0)(_ + _)

	val numAttempted = results.map(s => if (s.attempted) { 1 } else { 0 }).foldLeft(0)(_ + _)

	val questions = results.toVector.map(_.question)

	def nextQuestion(question: Question) = questions.elementAfter(question)

}

case class QuestionResults(question: Question, answers: List[Answer]) {

	val correct = answers.foldLeft(false)(_ || _.correct)

	def attempted = answers.nonEmpty
	
	val status = Status(attempted, correct)
}



