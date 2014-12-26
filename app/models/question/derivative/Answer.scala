package models.question.derivative

import com.artclod.mathml.scalar.MathMLElem
import models.question.ViewableMath
import models.support.{Owned, AnswerId, QuestionId, UserId}
import org.joda.time.DateTime

case class Answer(id: AnswerId, ownerId: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, correctNum: Short, creationDate: DateTime) extends ViewableMath with Owned {
  // We need to count number of correct answers in the db, so we store correct as a number with { 0 -> false, 1 -> true }
  if(correctNum != 0 && correctNum != 1) { correctNumError }

  def correct : Boolean = correctNum match {
    case 0 => false
    case 1 => true
    case _ => correctNumError
  }

  private def correctNumError = throw new IllegalStateException("In " + this + " correctNum was [" + correctNum + "] can only be in { 0 -> false, 1 -> true }, coding error")
}

object UnfinishedAnswer {
  def apply(ownerId: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, creationDate: DateTime)(correct: Boolean): Answer =
    Answer(null, ownerId, questionId, mathML, rawStr, if(correct) 1 else 0, creationDate)
}