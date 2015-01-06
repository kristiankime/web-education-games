package models.question.derivative.result

import models.question.{QuestionResults, QuestionScoring, Status}
import models.question.derivative.{DerivativeAnswer, DerivativeQuestion}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.Session
import service.User
import viewsupport.question.derivative.{Correct, Ongoing, Unstarted}

case class DerivativeQuestionResults(answerer: User, question: DerivativeQuestion, answers: List[DerivativeAnswer]) extends QuestionResults {
  def teacherScore(studentSkill: Double): Double = QuestionScoring.teacherScore(question, correct, studentSkill)
}
