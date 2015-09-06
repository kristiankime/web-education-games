package controllers.quiz

import com.artclod.collection.MustHandle
import com.artclod.mathml.{MathMLEq, MathMLRange, MathMLDefined}
import com.artclod.mathml.scalar.MathMLElem
import controllers.quiz.graphmatch.GraphMatchQuestionForm
import play.api.data.Form
import controllers.quiz.derivative.DerivativeQuestionForm
import controllers.quiz.derivativegraph.DerivativeGraphQuestionForm
import controllers.quiz.tangent.TangentQuestionForm

case class QuestionForms(derivative: Form[DerivativeQuestionForm], derivativeGraph: Form[DerivativeGraphQuestionForm], tangent: Form[TangentQuestionForm], graphMatch: Form[GraphMatchQuestionForm]) {

//  this .asInstanceOf[Tuple4[Form[DerivativeQuestionForm],Form[DerivativeGraphQuestionForm],Form[TangentQuestionForm],Form[GraphMatchQuestionForm]]]

  def mustHandle = MustHandle.fromTuple( QuestionForms.unapply(this).get )

//  def forms = mustHandle ->

//  def forms = List(derivative, derivativeGraph, tangent, graphMath)

//  def errorIndex = com.artclod.play.errorIndex(forms: _*)

  def errorIndex = {
    val errors = mustHandle -> (_.hasErrors, _.hasErrors, _.hasErrors, _.hasErrors)
    val firstTrue = com.artclod.play.firstTrue(errors)
    firstTrue
  }

  def errorIndexOrZero = {
    math.max(0, errorIndex)
  }

//  def hasErrors = com.artclod.play.hasError(forms: _*)

  def hasErrors = errorIndex > 0

//  def editor(name: String) =  views.html.tag.switch(name, com.artclod.play.errorIndex(derivative, derivativeGraph, tangent))

//  @tag.switch("createQuestion", com.artclod.play.errorIndex(derivativeForm, derivativeGraphForm, tangentForm),
//    ("Create a Derivative Question", views.html.quiz.derivative.questionEditor(QuestionsController.createDerivative(course.organizationId, course.id, quiz.id))),
//    ("Create a Derivative Graph Question", views.html.quiz.derivativegraph.questionEditor(QuestionsController.createDerivativeGraph(course.organizationId, course.id, quiz.id), derivativeGraphForm)),
//    ("Create a Tangent Question", views.html.quiz.tangent.questionEditor(QuestionsController.createTangent(course.organizationId, course.id, quiz.id), tangentForm))
//  )

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
