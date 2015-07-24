package controllers.quiz

import play.api.data.Form
import controllers.quiz.derivative.DerivativeQuestionForm
import controllers.quiz.derivativegraph.DerivativeGraphQuestionForm
import controllers.quiz.tangent.TangentQuestionForm

case class QuestionForms(derivative: Form[DerivativeQuestionForm], derivativeGraph: Form[DerivativeGraphQuestionForm], tangent: Form[TangentQuestionForm]) {

  def forms = List(derivative, derivativeGraph, tangent)

  def errorIndex = com.artclod.play.errorIndex(forms: _*)

  def hasErrors = com.artclod.play.hasError(forms: _*)

//  def editor(name: String) =  views.html.tag.switch(name, com.artclod.play.errorIndex(derivative, derivativeGraph, tangent))

//  @tag.switch("createQuestion", com.artclod.play.errorIndex(derivativeForm, derivativeGraphForm, tangentForm),
//    ("Create a Derivative Question", views.html.quiz.derivative.questionEditor(QuestionsController.createDerivative(course.organizationId, course.id, quiz.id))),
//    ("Create a Derivative Graph Question", views.html.quiz.derivativegraph.questionEditor(QuestionsController.createDerivativeGraph(course.organizationId, course.id, quiz.id), derivativeGraphForm)),
//    ("Create a Tangent Question", views.html.quiz.tangent.questionEditor(QuestionsController.createTangent(course.organizationId, course.id, quiz.id), tangentForm))
//  )

}

object QuestionForms {

  val empty = QuestionForms(DerivativeQuestionForm.values, DerivativeGraphQuestionForm.values, TangentQuestionForm.values)

  def derivative(derivative: Form[DerivativeQuestionForm]) = QuestionForms(derivative, DerivativeGraphQuestionForm.values, TangentQuestionForm.values)

  def derivativeGraph(derivativeGraph: Form[DerivativeGraphQuestionForm]) = QuestionForms(DerivativeQuestionForm.values, derivativeGraph, TangentQuestionForm.values)

  def tangent(tangent: Form[TangentQuestionForm]) = QuestionForms(DerivativeQuestionForm.values, DerivativeGraphQuestionForm.values, tangent)

}
