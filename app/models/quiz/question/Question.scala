package models.quiz.question

import java.io.File

import com.artclod.markup.{MarkupParser, LaikaParser}
import com.artclod.mathml.scalar.{Cn, MathMLElem, x}
import com.artclod.slick.JodaUTC
import controllers.quiz.derivative.DerivativeQuestionForm
import controllers.quiz.tangent.TangentQuestionForm
import models.organization.Course
import models.quiz._
import models.quiz.answer.{Answer, TangentAnswer}
import models.quiz.question.support.DerivativeOrder
import models.quiz.question.support._
import models.support.{Owned, QuestionId, QuizId, UserId}
import models.user.User
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.Session
import play.api.templates.Html
import service.Access

import scala.util.Random

sealed trait Question extends Owned {
  val id: QuestionId
  val ownerId: UserId
  val creationDate: DateTime
  val atCreationDifficulty : Double

  def access(course: Course)(implicit user: User, session: Session) = {
    val courseAccess = course.access
    val ownerAccess = Access(user, ownerId)
    Seq(courseAccess, ownerAccess).max
  }

  def quiz(quizId: QuizId)(implicit session: Session) : Option[Quiz]

  def results(user: User)(implicit session: Session) : QuestionResults

  def answersAndOwners(implicit session: Session) : List[(Answer, User)]

  def difficulty : Double

  def display(explanation : Boolean = true)(implicit user: models.user.User, session: play.api.db.slick.Config.driver.simple.Session) : Html

  def isAttached(quizId: QuizId)(implicit session: Session) : Boolean = Quizzes.questions(quizId).map(_.id).toSet.contains(id)

  def attach(quizId: QuizId)(implicit session: Session) : Unit = if(!isAttached(quizId)){ attachUnsafe(quizId ) }

  protected def attachUnsafe(quizId: QuizId)(implicit session: Session) : Unit
}

case class DerivativeQuestion(id: QuestionId, ownerId: UserId, mathML: MathMLElem, rawStr: String, creationDate: DateTime, atCreationDifficulty : Double, order: Int = 1) extends Question with ViewableMath {

  def answersAndOwners(implicit session: Session) = DerivativeQuestions.answersAndOwners(id)

  def difficulty : Double = DerivativeQuestionDifficulty(mathML)

  def results(user: User)(implicit session: Session) = DerivativeQuestionResults(user, this, answers(user))

  def answers(user: User)(implicit session: Session) = DerivativeQuestions(id, user)

  def display(explanation : Boolean = true)(implicit user: models.user.User, session: play.api.db.slick.Config.driver.simple.Session) : Html = views.html.quiz.derivative.questionDisplay(this, explanation)

  def quiz(quizId: QuizId)(implicit session: Session) : Option[Quiz] = DerivativeQuestions.quizFor(id, quizId)

  protected def attachUnsafe(quizId: QuizId)(implicit session: Session) = DerivativeQuestions.attach(this, quizId)
}

case class TangentQuestion(id: QuestionId, ownerId: UserId, function: MathMLElem, functionStr: String, atPointX: MathMLElem, atPointXStr: String, creationDate: DateTime, atCreationDifficulty : Double, order: Int = 1) extends Question {

  def answersAndOwners(implicit session: Session) : List[(TangentAnswer, User)] = TangentQuestions.answersAndOwners(id)

  def difficulty : Double = TangentQuestionDifficulty(function)

  def results(user: User)(implicit session: Session) = TangentQuestionResults(user, this, answers(user))

  def answers(user: User)(implicit session: Session) = TangentQuestions(id, user)

  def display(explanation : Boolean = true)(implicit user: models.user.User, session: play.api.db.slick.Config.driver.simple.Session) : Html = views.html.quiz.tangent.questionDisplay(this, explanation)

  def quiz(quizId: QuizId)(implicit session: Session) : Option[Quiz] = TangentQuestions.quizFor(id, quizId)

  def functionViewableMath = new ViewableMath { val mathML = function; val rawStr = functionStr }

  def atPointXViewableMath = new ViewableMath { val mathML = atPointX; val rawStr = atPointXStr }

  protected def attachUnsafe(quizId: QuizId)(implicit session: Session) = TangentQuestions.attach(this, quizId)
}

case class DerivativeGraphQuestion(id: QuestionId, ownerId: UserId, function: MathMLElem, functionStr: String, showFunction: Boolean, creationDate: DateTime, derivativeOrder: DerivativeOrder, atCreationDifficulty : Double, order: Int = 1) extends Question with ViewableMath {

  def answersAndOwners(implicit session: Session) = DerivativeGraphQuestions.answersAndOwners(id)

  def difficulty : Double = DerivativeGraphQuestionDifficulty(function, showFunction)

  def results(user: User)(implicit session: Session) = DerivativeGraphQuestionResults(user, this, answers(user))

  def answers(user: User)(implicit session: Session) = DerivativeGraphQuestions(id, user)

  def display(explanation : Boolean = true)(implicit user: models.user.User, session: play.api.db.slick.Config.driver.simple.Session) : Html = views.html.quiz.derivativegraph.questionDisplay(this, explanation)

  def quiz(quizId: QuizId)(implicit session: Session) : Option[Quiz] = DerivativeGraphQuestions.quizFor(id, quizId)

  protected def attachUnsafe(quizId: QuizId)(implicit session: Session) = DerivativeGraphQuestions.attach(this, quizId)

  val mathML = function
  val rawStr = functionStr

  def functionMathJs = function.toMathJS
  def firstDerivativeMathJS = function.dx.toMathJS
  def secondDerivativeMathJS = function.dx.dx.toMathJS

  def mathJS = derivativeOrder match {
    case FuncFirstSecond => (functionMathJs, firstDerivativeMathJS, secondDerivativeMathJS)
    case FuncSecondFirst => (functionMathJs, secondDerivativeMathJS, firstDerivativeMathJS)
    case FirstFuncSecond => (firstDerivativeMathJS, functionMathJs, secondDerivativeMathJS)
    case FirstSecondFunc => (firstDerivativeMathJS, secondDerivativeMathJS, functionMathJs)
    case SecondFuncFirst => (secondDerivativeMathJS, functionMathJs, firstDerivativeMathJS)
    case SecondFirstFunc => (secondDerivativeMathJS, firstDerivativeMathJS, functionMathJs)
  }
}

case class GraphMatchQuestion(id: QuestionId, ownerId: UserId, function1Math: MathMLElem, function1Raw: String, function2Math: MathMLElem, function2Raw: String, function3Math: MathMLElem, function3Raw: String, graphThis: Short, creationDate: DateTime, atCreationDifficulty : Double, order: Int = 1) extends Question {
  GraphMatchQuestionWhich.validWhichValue(graphThis)

  def answersAndOwners(implicit session: Session) = GraphMatchQuestions.answersAndOwners(id)

  def difficulty : Double = GraphMatchQuestionDifficulty(function1Math, function2Math, function3Math, graphThis)

  def results(user: User)(implicit session: Session) = GraphMatchQuestionResults(user, this, answers(user))

  def answers(user: User)(implicit session: Session) = GraphMatchQuestions(id, user)

  def display(explanation : Boolean = true)(implicit user: models.user.User, session: play.api.db.slick.Config.driver.simple.Session) : Html = views.html.quiz.graphmatch.questionDisplay(this, explanation)

  def quiz(quizId: QuizId)(implicit session: Session) : Option[Quiz] = GraphMatchQuestions.quizFor(id, quizId)

  protected def attachUnsafe(quizId: QuizId)(implicit session: Session) = GraphMatchQuestions.attach(this, quizId)

  def mathView1 : models.quiz.ViewableMath = new models.quiz.ViewableMath { val mathML = function1Math; val rawStr = function1Raw;  }

  def mathView2 : models.quiz.ViewableMath = new models.quiz.ViewableMath { val mathML = function2Math; val rawStr = function2Raw;  }

  def mathView3 : models.quiz.ViewableMath = new models.quiz.ViewableMath { val mathML = function3Math; val rawStr = function3Raw;  }

  def graphThisMathJs = graphThis match {
    case 1 => function1Math.toMathJS
    case 2 => function2Math.toMathJS
    case 3 => function3Math.toMathJS
    case _ => throw new IllegalStateException("graphThis was " + graphThis + " should have been between " + GraphMatchQuestionWhich.whichMin + " and " + GraphMatchQuestionWhich.whichMax)
  }
}

object GraphMatchQuestionWhich {
  val whichMin = 1;
  val whichMax = 3;
  implicit val randomEngine = new Random(JodaUTC.now.getMillis())

  def randomWhichValue = whichMax + randomEngine.nextInt(whichMax - whichMin)

  def validWhichValue(v: Short): Unit = {
    if(v < whichMin) throw new IllegalStateException("graphThis was " + v + " should have been between " + GraphMatchQuestionWhich.whichMin + " and " + GraphMatchQuestionWhich.whichMax)
    if(v > whichMax) throw new IllegalStateException("graphThis was " + v + " should have been between " + GraphMatchQuestionWhich.whichMin + " and " + GraphMatchQuestionWhich.whichMax)
  }
}

case class PolynomialZoneQuestion(id: QuestionId, ownerId: UserId, roots: Vector[Int], scale: Double, zoneType: PolynomialZoneType, creationDate: DateTime, atCreationDifficulty : Double, order: Int = 1) extends Question {

  def answersAndOwners(implicit session: Session) = PolynomialZoneQuestions.answersAndOwners(id)

  def difficulty : Double = PolynomialZoneQuestionDifficulty(roots, scale, zoneType)

  def results(user: User)(implicit session: Session) = PolynomialZoneQuestionResults(user, this, answers(user))

  def answers(user: User)(implicit session: Session) = PolynomialZoneQuestions(id, user)

  def display(explanation : Boolean = true)(implicit user: models.user.User, session: play.api.db.slick.Config.driver.simple.Session) : Html = views.html.quiz.polynomialzone.questionDisplay(this, explanation)

  def quiz(quizId: QuizId)(implicit session: Session) : Option[Quiz] = PolynomialZoneQuestions.quizFor(id, quizId)

  protected def attachUnsafe(quizId: QuizId)(implicit session: Session) = PolynomialZoneQuestions.attach(this, quizId)

  def polynomial : MathMLElem =
    if(roots.isEmpty) {
      Cn(scale)
    } else {
      val mathRoots = roots.map(v => Cn(v) : MathMLElem)
      val rootTerms = mathRoots.map(r => (x - r) : MathMLElem)
      val poly = Cn(scale) * rootTerms.reduce( (a , b ) => a * b)
      poly
    }
}

case class MultipleChoiceQuestion(id: QuestionId, ownerId: UserId, description: String, explanationRaw: String, explanationHtml: Html, correctAnswer: Short, creationDate: DateTime, atCreationDifficulty : Double, order: Int = 1) extends Question {

  def answerOptions(implicit session: Session): List[MultipleChoiceQuestionOption] = MultipleChoiceQuestionOptions(this)

  def answersAndOwners(implicit session: Session) = MultipleChoiceQuestions.answersAndOwners(id)

  def difficulty : Double = atCreationDifficulty

  def results(user: User)(implicit session: Session) = MultipleChoiceQuestionResults(user, this, answers(user))

  def answers(user: User)(implicit session: Session) = MultipleChoiceQuestions(id, user)

  def display(explanation : Boolean = true)(implicit user: models.user.User, session: play.api.db.slick.Config.driver.simple.Session) : Html = views.html.quiz.multiplechoice.questionDisplay(this, explanation)

  def quiz(quizId: QuizId)(implicit session: Session) : Option[Quiz] = MultipleChoiceQuestions.quizFor(id, quizId)

  protected def attachUnsafe(quizId: QuizId)(implicit session: Session) = MultipleChoiceQuestions.attach(this, quizId)
}

case class MultipleChoiceQuestionOption(id: Long, questionId: QuestionId, optionRaw: String, optionHtml: Html)

case class MultipleFunctionQuestion(id: QuestionId, ownerId: UserId, description: String, explanationRaw: String, explanationHtml: Html, creationDate: DateTime, atCreationDifficulty : Double, order: Int = 1) extends Question {

  def answerOptions(implicit session: Session): List[MultipleFunctionQuestionOption] = MultipleFunctionQuestionOptions(this)

  def answersAndOwners(implicit session: Session) = MultipleFunctionQuestions.answersAndOwners(id)

  def difficulty : Double = atCreationDifficulty

  def results(user: User)(implicit session: Session) = MultipleFunctionQuestionResults(user, this, answers(user))

  def answers(user: User)(implicit session: Session) = MultipleFunctionQuestions(id, user)

  def display(explanation : Boolean = true)(implicit user: models.user.User, session: play.api.db.slick.Config.driver.simple.Session) : Html = views.html.quiz.multiplefunction.questionDisplay(this, explanation)

  def display(currentAnswer: Option[Either[models.quiz.answer.MultipleFunctionAnswer, models.quiz.answer.MultipleFunctionAnswer]])(implicit user: models.user.User, session: play.api.db.slick.Config.driver.simple.Session) : Html = views.html.quiz.multiplefunction.questionDisplayDetailed(this, currentAnswer)

  def quiz(quizId: QuizId)(implicit session: Session) : Option[Quiz] = MultipleFunctionQuestions.quizFor(id, quizId)

  protected def attachUnsafe(quizId: QuizId)(implicit session: Session) = MultipleFunctionQuestions.attach(this, quizId)
}

case class MultipleFunctionQuestionOption(id: Long, questionId: QuestionId, optionRaw: String, optionHtml: Html, functionMath: MathMLElem, functionRaw: String)