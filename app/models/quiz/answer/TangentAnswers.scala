package models.quiz.answer

import com.artclod.mathml.MathMLEq.{doubleCloseEnough, matchCombine}
import com.artclod.mathml.scalar.MathMLElem
import models.quiz.question.TangentQuestion
import models.quiz.table._
import play.api.db.slick.Config.driver.simple._

object TangentAnswers {

  def correct(question: TangentQuestion, slopeAnswer: MathMLElem, interceptAnswer: MathMLElem) = {
    // TODO better error handling on .gets
    val x0 = question.atPointX.eval().get
    val m = question.function.dx.evalT("x" -> x0).get
    val y0 = question.function.evalT("x" -> x0).get

    matchCombine( doubleCloseEnough(slopeAnswer.eval().get, m), doubleCloseEnough(interceptAnswer.eval().get, -(m * x0) + y0) )
  }

  // ======= CREATE ======
  def createAnswer(answer: TangentAnswer)(implicit session: Session) = {
    val toInsert = answer.copy(id = AnswerIdNext())
    tangentAnswersTable += toInsert
    toInsert
  }
}
