package models.quiz.answer.result

import models.quiz.answer.TangentAnswer
import models.quiz.question.{QuestionResults, TangentQuestion}
import models.user.UserSetting
import service.Login

case class TangentQuestionResults(answerer: UserSetting, question: TangentQuestion, answers: List[TangentAnswer]) extends QuestionResults {
  def teacherScore(studentSkill: Double): Double = 0d // TODO  QuestionScoring.teacherScore(question, correct, studentSkill)
}
