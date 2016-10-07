package models.quiz.answer

import com.artclod.mathml.{Match, Yes, No, Inconclusive}
import com.artclod.math.Interval
import com.artclod.mathml.scalar.MathMLElem
import models.quiz.ViewableMath
import models.quiz.question.support.DerivativeOrder
import models.support.{AnswerId, Owned, QuestionId, UserId}
import org.joda.time.DateTime
import play.api.templates.Html
import play.api.db.slick.Config.driver.simple.Session
import views.html.mathml.correct

trait Answer extends Owned {
  val id: AnswerId
  val ownerId: UserId
  val questionId: QuestionId
  val comment: String
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
  def display(implicit session: Session) : Html
}

// ==== Derivative ===
case class DerivativeAnswer(id: AnswerId, ownerId: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, comment: String, correctNum: Short, creationDate: DateTime) extends Answer with ViewableMath {
  def display(implicit session: Session) : Html = views.html.quiz.derivative.answerDisplay(this)
}

object DerivativeAnswerUnfinished {
  def apply(ownerId: UserId, questionId: QuestionId, mathML: MathMLElem, rawStr: String, comment: String, creationDate: DateTime)(correct: Boolean): DerivativeAnswer =
    DerivativeAnswer(null, ownerId, questionId, mathML, rawStr, comment, if(correct) 1 else 0, creationDate)
}

// ==== Tangent ===
case class TangentAnswer(id: AnswerId, ownerId: UserId, questionId: QuestionId, slopeMathML: MathMLElem, slopeRawStr: String, interceptMathML: MathMLElem, interceptRawStr: String, comment: String, correctNum: Short, creationDate: DateTime) extends Answer {
  def display(implicit session: Session) : Html = views.html.quiz.tangent.answerDisplay(this)

  def slope = new ViewableMath { val mathML = slopeMathML; val rawStr = slopeRawStr }

  def intercept = new ViewableMath { val mathML = interceptMathML; val rawStr = interceptRawStr }
}

object TangentAnswerUnfinished {
  def apply(ownerId: UserId, questionId: QuestionId, slopeMathML: MathMLElem, slopeRawStr: String, interceptMathML: MathMLElem, interceptRawStr: String, comment: String, creationDate: DateTime)(correct: Boolean): TangentAnswer =
    TangentAnswer(null, ownerId, questionId, slopeMathML, slopeRawStr, interceptMathML, interceptRawStr, comment, if(correct) 1 else 0, creationDate)
}

// ==== Derivative Graph ===
case class DerivativeGraphAnswer(id: AnswerId, ownerId: UserId, questionId: QuestionId, derivativeOrder: DerivativeOrder, comment: String, correctNum: Short, creationDate: DateTime) extends Answer {
  def display(implicit session: Session) : Html = views.html.quiz.derivativegraph.answerDisplay(this)
}

object DerivativeGraphAnswerUnfinished {
  def apply(ownerId: UserId, questionId: QuestionId, derivativeOrder: DerivativeOrder, comment: String, creationDate: DateTime)(correct: Boolean): DerivativeGraphAnswer =
    DerivativeGraphAnswer(null, ownerId, questionId, derivativeOrder, comment, if(correct) 1 else 0, creationDate)
}

// ==== Graph Match ===
case class GraphMatchAnswer(id: AnswerId, ownerId: UserId, questionId: QuestionId, guessIndex: Short, comment: String, correctNum: Short, creationDate: DateTime) extends Answer {
  def display(implicit session: Session) : Html = views.html.quiz.graphmatch.answerDisplay(this)
}

object GraphMatchAnswerUnfinished {
  def apply(ownerId: UserId, questionId: QuestionId, guessIndex: Short, comment: String, creationDate: DateTime)(correct: Boolean): GraphMatchAnswer =
    GraphMatchAnswer(null, ownerId, questionId, guessIndex, comment, if(correct) 1 else 0, creationDate)
}

// ==== Polynomial Zone ===
case class PolynomialZoneAnswer(id: AnswerId, ownerId: UserId, questionId: QuestionId, zones: Vector[Interval], correctNum: Short, comment: String, creationDate: DateTime) extends Answer {
  if(Interval.overlap(zones)) { throw new IllegalStateException("Zones were overlapping " + zones) }

  def display(implicit session: Session) : Html = views.html.quiz.polynomialzone.answerDisplay(this)

  def zoneToString = zones.mkString(",")
}

object PolynomialZoneAnswerUnfinished {
  def apply(ownerId: UserId, questionId: QuestionId, zones: Vector[Interval], comment: String, creationDate: DateTime)(correct: Boolean): PolynomialZoneAnswer =
    PolynomialZoneAnswer(null, ownerId, questionId, zones, if(correct) 1 else 0, comment, creationDate)
}

// ==== Multiple Choice ===
case class MultipleChoiceAnswer(id: AnswerId, ownerId: UserId, questionId: QuestionId, guessIndex: Short, comment: String, correctNum: Short, creationDate: DateTime) extends Answer {

  def guessIndexDisplay = guessIndex + 1

  def display(implicit session: Session) : Html = views.html.quiz.multiplechoice.answerDisplay(this)
}

object MultipleChoiceAnswerUnfinished {
  def apply(ownerId: UserId, questionId: QuestionId, guessIndex: Short, comment: String, creationDate: DateTime)(correct: Boolean): MultipleChoiceAnswer =
    MultipleChoiceAnswer(null, ownerId, questionId, guessIndex, comment, if(correct) 1 else 0, creationDate)
}


// ==== Multiple Function ===
case class MultipleFunctionAnswer(id: AnswerId, ownerId: UserId, questionId: QuestionId, comment: String, correctNum: Short, creationDate: DateTime) extends Answer {

  def options(implicit session: Session) = MultipleFunctionAnswerOptions(this)

  def display(implicit session: Session) : Html = views.html.quiz.multiplefunction.answerDisplay(this)
}

object MultipleFunctionAnswerUnfinished {
  def apply(ownerId: UserId, questionId: QuestionId, comment: String, creationDate: DateTime, guesses: List[String])(correct: Boolean): MultipleFunctionAnswer =
    MultipleFunctionAnswer(null, ownerId, questionId, comment, if(correct) 1 else 0, creationDate)
}

case class MultipleFunctionAnswerOption(id: Long, answerId: AnswerId, functionMath: MathMLElem, functionRaw: String, correctNum: Short)