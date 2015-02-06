package models.quiz.answer

import com.artclod.mathml.MathMLEq.{ doubleCloseEnough, matchCombine }
import com.artclod.mathml.scalar.{Cn, MathMLElem, x}
import models.quiz.question.TangentQuestion
import models.quiz.table._
import play.api.db.slick.Config.driver.simple._

object TangentAnswers {

    //  def correct(question: TangentQuestion, slopeAnswer: MathMLElem, interceptAnswer: MathMLElem) = {
    //    val answerEquation = (slopeAnswer * x) + interceptAnswer
    //
    //    // TODO better error handling on .gets
    //    val x0 = question.atPointX.eval().get
    //    val m = Cn(question.function.dx.eval(Map("x" -> x0)).get)
    //    val y0 = question.function.eval(Map("x" -> x0)).get
    //    val tangent = (m * x) - (m * x0) + y0
    //
    //    MathMLEq.checkEq("x", tangent, answerEquation)
    //  }

  def correct(question: TangentQuestion, slopeAnswer: MathMLElem, interceptAnswer: MathMLElem) = {
    // TODO better error handling on .gets
    val x0 = question.atPointX.eval.get
    val m = question.function.dx.eval("x" -> x0).get
    val y0 = question.function.eval("x" -> x0).get

    matchCombine( doubleCloseEnough(slopeAnswer.eval.get, m), doubleCloseEnough(interceptAnswer.eval.get, -(m * x0) + y0) )
  }

  // ======= CREATE ======
  def createAnswer(answer: TangentAnswer)(implicit session: Session) = {
    val answerId = (tangentAnswersTable returning tangentAnswersTable.map(_.id)) += answer
    answer.copy(id = answerId)
  }
}
