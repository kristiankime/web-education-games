package models.question.tangent

import com.artclod.mathml.scalar.MathMLElem
import models.question.ViewableMath
import models.support.{Owned, AnswerId, QuestionId, UserId}
import org.joda.time.DateTime

case class TangentAnswer(id: AnswerId, ownerId: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, correctNum: Short, creationDate: DateTime) extends ViewableMath with Owned {

}

object TangentAnswerUnfinished {
  def apply(ownerId: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, creationDate: DateTime)(correct: Boolean): TangentAnswer =
    TangentAnswer(null, ownerId, questionId, mathML, rawStr, if(correct) 1 else 0, creationDate)
}