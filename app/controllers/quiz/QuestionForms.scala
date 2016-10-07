package controllers.quiz

import com.artclod.collection.MustHandle
import com.artclod.mathml.{MathMLEq, MathMLRange, MathMLDefined}
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.slick.JodaUTC
import com.artclod.xml.Nodes
import play.api.data.Form
import controllers.quiz.graphmatch.GraphMatchQuestionForm
import controllers.quiz.multiplechoice.MultipleChoiceQuestionForm
import controllers.quiz.multiplefunction.MultipleFunctionQuestionForm
import controllers.quiz.polynomialzone.PolynomialZoneQuestionForm
import controllers.quiz.derivative.DerivativeQuestionForm
import controllers.quiz.derivativegraph.DerivativeGraphQuestionForm
import controllers.quiz.tangent.TangentQuestionForm
import com.artclod.tuple._

import scala.util.Random

case class QuestionForms(derivative: Form[DerivativeQuestionForm],
                         derivativeGraph: Form[DerivativeGraphQuestionForm],
                         tangent: Form[TangentQuestionForm],
                         graphMatch: Form[GraphMatchQuestionForm],
                         polynomialZone: Form[PolynomialZoneQuestionForm],
                         multipleChoice: Form[MultipleChoiceQuestionForm],
                         multipleFunction: Form[MultipleFunctionQuestionForm]) {
  implicit val randomEngine = new Random(JodaUTC.now.getMillis())

  def mustHandle = MustHandle.fromTuple( QuestionForms.unapply(this).get )

  def errorIndex = {
    val errors = mustHandle -> (_.hasErrors, _.hasErrors, _.hasErrors, _.hasErrors, _.hasErrors, _.hasErrors, _.hasErrors)
    val firstTrue = com.artclod.play.firstTrue(errors)
    firstTrue
  }

  def errorIndexOrRandom =
    if (errorIndex != -1) {
      errorIndex
    } else {
      randomEngine.nextInt(mustHandle.size)
    }

  def hasErrors = (errorIndex != -1)

  def allErrors = QuestionForms.unapply(this).get.toList(_.errors.toList, _.errors.toList, _.errors.toList, _.errors.toList, _.errors.toList, _.errors.toList, _.errors.toList )
}

object QuestionForms {

  val empty = QuestionForms(DerivativeQuestionForm.values, DerivativeGraphQuestionForm.values, TangentQuestionForm.values, GraphMatchQuestionForm.values, PolynomialZoneQuestionForm.values, MultipleChoiceQuestionForm.values, MultipleFunctionQuestionForm.values)

  def derivative(derivative: Form[DerivativeQuestionForm]) = QuestionForms(derivative, DerivativeGraphQuestionForm.values, TangentQuestionForm.values, GraphMatchQuestionForm.values, PolynomialZoneQuestionForm.values, MultipleChoiceQuestionForm.values, MultipleFunctionQuestionForm.values)

  def derivativeGraph(derivativeGraph: Form[DerivativeGraphQuestionForm]) = QuestionForms(DerivativeQuestionForm.values, derivativeGraph, TangentQuestionForm.values, GraphMatchQuestionForm.values, PolynomialZoneQuestionForm.values, MultipleChoiceQuestionForm.values, MultipleFunctionQuestionForm.values)

  def tangent(tangent: Form[TangentQuestionForm]) = QuestionForms(DerivativeQuestionForm.values, DerivativeGraphQuestionForm.values, tangent, GraphMatchQuestionForm.values, PolynomialZoneQuestionForm.values, MultipleChoiceQuestionForm.values, MultipleFunctionQuestionForm.values)

  def graphMatch(graphMath: Form[GraphMatchQuestionForm]) = QuestionForms(DerivativeQuestionForm.values, DerivativeGraphQuestionForm.values, TangentQuestionForm.values, graphMath, PolynomialZoneQuestionForm.values, MultipleChoiceQuestionForm.values, MultipleFunctionQuestionForm.values)

  def polynomialZone(polynomialZone: Form[PolynomialZoneQuestionForm]) = QuestionForms(DerivativeQuestionForm.values, DerivativeGraphQuestionForm.values, TangentQuestionForm.values, GraphMatchQuestionForm.values, polynomialZone, MultipleChoiceQuestionForm.values, MultipleFunctionQuestionForm.values)

  def multipleChoice(multipleChoice: Form[MultipleChoiceQuestionForm]) = QuestionForms(DerivativeQuestionForm.values, DerivativeGraphQuestionForm.values, TangentQuestionForm.values, GraphMatchQuestionForm.values, PolynomialZoneQuestionForm.values, multipleChoice, MultipleFunctionQuestionForm.values)

  def multipleFunction(multipleFunction: Form[MultipleFunctionQuestionForm]) = QuestionForms(DerivativeQuestionForm.values, DerivativeGraphQuestionForm.values, TangentQuestionForm.values, GraphMatchQuestionForm.values, PolynomialZoneQuestionForm.values, MultipleChoiceQuestionForm.values, multipleFunction)

  def verifyFunctionValid(f: MathMLElem) = MathMLDefined.isDefinedFor(f, .10d)

  def verifyFunctionDerivativeIsEasyToType(f: MathMLElem) = Nodes.nodeCount(f.dx.simplify) <= 60

  def verifyFunctionDisplaysNicely(f: MathMLElem) = {
    val ret = MathMLRange.percentInRange("x", f, -1d * MathMLEq.tightRange, MathMLEq.tightRange) >= .10d
    ret
  }

}
