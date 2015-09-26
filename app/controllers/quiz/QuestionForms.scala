package controllers.quiz

import com.artclod.collection.MustHandle
import com.artclod.mathml.{MathMLEq, MathMLRange, MathMLDefined}
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.slick.JodaUTC
import controllers.quiz.graphmatch.GraphMatchQuestionForm
import play.api.data.Form
import controllers.quiz.derivative.DerivativeQuestionForm
import controllers.quiz.derivativegraph.DerivativeGraphQuestionForm
import controllers.quiz.tangent.TangentQuestionForm

import scala.util.Random

case class QuestionForms(derivative: Form[DerivativeQuestionForm], derivativeGraph: Form[DerivativeGraphQuestionForm], tangent: Form[TangentQuestionForm], graphMatch: Form[GraphMatchQuestionForm]) {
  implicit val randomEngine = new Random(JodaUTC.now.getMillis())

  def mustHandle = MustHandle.fromTuple( QuestionForms.unapply(this).get )

  def errorIndex = {
    val errors = mustHandle -> (_.hasErrors, _.hasErrors, _.hasErrors, _.hasErrors)
    val firstTrue = com.artclod.play.firstTrue(errors)
    firstTrue
  }

//  def errorIndexOrZero = {
//    math.max(0, errorIndex)
//  }

  def errorIndexOrRandom =
    if (errorIndex != -1) {
      errorIndex
    } else {
      randomEngine.nextInt(mustHandle.size)
    }

  def hasErrors = (errorIndex != -1)

}

object QuestionForms {

  val empty = QuestionForms(DerivativeQuestionForm.values, DerivativeGraphQuestionForm.values, TangentQuestionForm.values, GraphMatchQuestionForm.values)

  def derivative(derivative: Form[DerivativeQuestionForm]) = QuestionForms(derivative, DerivativeGraphQuestionForm.values, TangentQuestionForm.values, GraphMatchQuestionForm.values)

  def derivativeGraph(derivativeGraph: Form[DerivativeGraphQuestionForm]) = QuestionForms(DerivativeQuestionForm.values, derivativeGraph, TangentQuestionForm.values, GraphMatchQuestionForm.values)

  def tangent(tangent: Form[TangentQuestionForm]) = QuestionForms(DerivativeQuestionForm.values, DerivativeGraphQuestionForm.values, tangent, GraphMatchQuestionForm.values)

  def graphMatch(graphMath: Form[GraphMatchQuestionForm]) = QuestionForms(DerivativeQuestionForm.values, DerivativeGraphQuestionForm.values, TangentQuestionForm.values, graphMath)

  def verifyFunctionValid(f: MathMLElem) = MathMLDefined.isDefinedFor(f, .10d)

  def verifyFunctionDisplaysNicely(f: MathMLElem) = {
    val ret = MathMLRange.percentInRange("x", f, -1d * MathMLEq.tightRange, MathMLEq.tightRange) >= .10d
    ret
  }

}
