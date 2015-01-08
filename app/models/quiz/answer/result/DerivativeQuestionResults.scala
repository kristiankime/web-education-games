package models.quiz.answer.result

import models.quiz.answer.DerivativeAnswer
import models.quiz.question.{QuestionScoring, QuestionResults, DerivativeQuestion}
import service.User

case class DerivativeQuestionResults(answerer: User, question: DerivativeQuestion, answers: List[DerivativeAnswer]) extends QuestionResults {
  def teacherScore(studentSkill: Double): Double = QuestionScoring.teacherScore(question, correct, studentSkill)
}
