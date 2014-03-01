package models.question.derivative.view

import models.question.derivative._
import service.User

case class QuizDetails(quiz: Quiz, studentResults: List[StudentResults])

case class StudentResults(student: User, studentQuestions: List[StudentQuestionResults])

case class StudentQuestionResults(question: Question, correct: Boolean, answers: List[Answer])
