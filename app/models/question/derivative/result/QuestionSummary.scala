package models.question.derivative.result

import com.artclod.mathml.scalar.MathMLElem
import models.question.{QuestionDifficulty, QuestionScore, ViewableMath}
import models.support._
import org.joda.time.DateTime

case class QuestionSummary(questionId: QuestionId, attempts: Int, mathML: MathMLElem, rawStr: String, correct: Boolean, firstAttempt: DateTime) extends ViewableMath {

  def difficulty : Double = QuestionDifficulty(mathML)

  def studentScore = if(correct) 1d else 0d

  def teacherScore(studentSkillLevel: Double) = QuestionScore.teacherScore(difficulty, correct, studentSkillLevel)

}
