package models.quiz.answer

import com.artclod.mathml.scalar.MathMLElem
import models.quiz.ViewableMath
import models.support.{AnswerId, QuestionId, UserId}
import org.joda.time.DateTime
import play.api.templates.Html

case class DerivativeAnswer(id: AnswerId, ownerId: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, correctNum: Short, creationDate: DateTime) extends Answer with ViewableMath {
  def display : Html = views.html.quiz.derivative.answerDisplay(this)
}

object DerivativeAnswerUnfinished {
  def apply(ownerId: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, creationDate: DateTime)(correct: Boolean): DerivativeAnswer =
    DerivativeAnswer(null, ownerId, questionId, mathML, rawStr, if(correct) 1 else 0, creationDate)
}