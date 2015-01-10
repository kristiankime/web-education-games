package models.quiz.answer.result

import models.quiz.answer.TangentAnswer
import models.quiz.question.{QuestionResults, TangentQuestion}
import service.User

case class TangentQuestionResults(answerer: User, question: TangentQuestion, answers: List[TangentAnswer]) extends QuestionResults {
  def teacherScore(studentSkill: Double): Double = 0d // TODO  QuestionScoring.teacherScore(question, correct, studentSkill)
}