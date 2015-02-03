package models.quiz.answer

import com.artclod.mathml.scalar.MathMLElem
import models.quiz.ViewableMath
import models.support.{AnswerId, QuestionId, UserId}
import org.joda.time.DateTime
import play.api.templates.Html
import models.quiz.ViewableMath

case class TangentAnswer(id: AnswerId, ownerId: UserId, questionId: QuestionId, slopeMathML: MathMLElem, slopeRawStr: String, interceptMathML: MathMLElem, interceptRawStr: String, correctNum: Short, creationDate: DateTime) extends Answer {
  def display : Html = views.html.quiz.tangent.answerDisplay(this)

  def slope = new ViewableMath { val mathML = slopeMathML; val rawStr = slopeRawStr }

  def intercept = new ViewableMath { val mathML = interceptMathML; val rawStr = interceptRawStr }

}

object TangentAnswerUnfinished {
  def apply(ownerId: UserId, questionId: QuestionId, slopeMathML: MathMLElem, slopeRawStr: String, interceptMathML: MathMLElem, interceptRawStr: String, creationDate: DateTime)(correct: Boolean): TangentAnswer =
    TangentAnswer(null, ownerId, questionId, slopeMathML, slopeRawStr, interceptMathML, interceptRawStr, if(correct) 1 else 0, creationDate)
}