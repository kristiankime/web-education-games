package models.question.derivative.view

import models.question.derivative._
import service.User

case class QuizDetails(quiz: Quiz, studentResults: List[StudentResults])

case class StudentResults(student: User, studentQuestions: List[StudentQuestionResults]) {
	def numQuestions = studentQuestions.size
	def numCorrect = studentQuestions.map(s => if (s.correct) { 1 } else { 0 }).foldLeft(0)(_ + _)
	def numAttempted = studentQuestions.map(s => if (s.attempted) { 1 } else { 0 }).foldLeft(0)(_ + _)
}

case class StudentQuestionResults(question: Question, correct: Boolean, answers: List[Answer]) {
	def attempted = answers.nonEmpty
}
