package models.question.tangent.result

import models.question.derivative.{DerivativeAnswer, DerivativeQuestion}
import models.question.tangent.{TangentAnswer, TangentQuestion}
import models.question.{QuestionResults, QuestionScoring}
import service.User

case class TangentQuestionResults(answerer: User, question: TangentQuestion, answers: List[TangentAnswer]) extends QuestionResults {
  def teacherScore(studentSkill: Double): Double = 0d // TODO  QuestionScoring.teacherScore(question, correct, studentSkill)
}
