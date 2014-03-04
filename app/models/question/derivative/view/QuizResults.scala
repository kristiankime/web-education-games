package models.question.derivative.view

import models.question.derivative._
import service.User

case class QuizResults(quiz: Quiz, results: List[UserQuizResults])

case class UserQuizResults(user: User, quiz: Quiz, results: List[QuestionResults]) {
	
	def numQuestions = results.size

	val numCorrect = results.map(s => if (s.correct) { 1 } else { 0 }).foldLeft(0)(_ + _)

	val numAttempted = results.map(s => if (s.attempted) { 1 } else { 0 }).foldLeft(0)(_ + _)

	val questions = results.toVector.map(_.question)

	def nextQuestion(question: Question): Option[Question] = {
		val index = questions.indexOf(question)
		if (index == -1) { None } // LATER should indicate difference between failure and end of list
		else if (index == (questions.size - 1)) { None }
		else { Some(questions(index + 1)) }
	}

}

case class QuestionResults(question: Question, answers: List[Answer]) {
	
	val correct = answers.foldLeft(false)(_ || _.correct)

	def attempted = answers.nonEmpty
}
