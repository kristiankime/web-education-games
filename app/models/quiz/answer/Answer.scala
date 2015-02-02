package models.quiz.answer

import models.support.{AnswerId, Owned, QuestionId, UserId}
import org.joda.time.DateTime
import play.api.templates.Html

trait Answer extends Owned {
  val id: AnswerId
  val ownerId: UserId
  val questionId: QuestionId
  val correctNum: Short
  val creationDate: DateTime

  // We need to count number of correct answers in the db, so we store correct as a number with { 0 -> false, 1 -> true }
  if(correctNum != 0 && correctNum != 1) { correctNumError }

  def correct : Boolean = correctNum match {
    case 0 => false
    case 1 => true
    case _ => correctNumError
  }

  private def correctNumError = throw new IllegalStateException("In " + this + " correctNum was [" + correctNum + "] can only be in { 0 -> false, 1 -> true }, coding error")

  // === Methods for viewing
  def display : Html
}