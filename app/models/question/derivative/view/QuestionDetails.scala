package models.question.derivative.view

import models.question.derivative._
import service.User

case class QuestionDetails(question: Question, correct: Boolean, answers: List[Answer])
