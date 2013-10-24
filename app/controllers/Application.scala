package controllers

import play.api._
import play.api.mvc._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.iteratee._
import models._
import akka.actor._
import scala.concurrent.duration._
import play.api.data._
import play.api.data.Forms._
import models.Equations
import mathml._

object Application extends Controller {

	def index = Action {
		Ok(views.html.index())
	}

	// ======== Equations ======== 
	def equations = Action {
		Ok(views.html.equations(Equations.all(), EquationHTML.form))
	}

	def newEquation = Action { implicit request =>
		EquationHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.equations(Equations.all(), errors)),
			equation => {
				Equations.create(equation)
				Redirect(routes.Application.equations)
			})
	}

	def deleteEquation(id: Int) = Action {
		Equations.delete(id);
		Ok(views.html.equations(Equations.all(), EquationHTML.form))
	}

	// ======== Self Quiz Questions ======== 
	def selfQuizQuestions = Action {
		Ok(views.html.self_quiz_list(DerivativeQuestions.all(), DerivativeQuestionHTML.form))
	}

	def selfQuizQuestion(id: Int) = Action {
		Ok(views.html.self_quiz_take(DerivativeQuestions.get(id).get, None))
	}

	def newSelfQuizQuestion = Action { implicit request =>
		DerivativeQuestionHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_list(DerivativeQuestions.all(), errors)),
			equation => {
				DerivativeQuestions.create(equation)
				Redirect(routes.Application.selfQuizQuestions)
			})
	}

	def deleteSelfQuizQuestion(id: Int) = Action {
		DerivativeQuestions.delete(id);
		Ok(views.html.self_quiz_list(DerivativeQuestions.all(), DerivativeQuestionHTML.form))
	}

	// ======== Self Quiz Answers ======== 
	def selfQuizAnswer(qid: Int, aid: Int) = Action {
		val question = DerivativeQuestions.get(qid).get // TODO can be null
		val answer = DerivativeQuestionAnswers.get(qid, aid)
		Ok(views.html.self_quiz_take(question, answer))
	}

	def answerSelfQuizQuestion = Action { implicit request =>
		DerivativeQuestionAnswerHTML.form.bindFromRequest.fold(
			// TODO currently we assume we can get the problem id here
			errors => {
				BadRequest(views.html.self_quiz_take(DerivativeQuestions.get(errors.get._1).get, None))
			},
			answerForm => {
				val question = DerivativeQuestions.get(answerForm._1).get // TODO check for no question here
				val answerStr = answerForm._2 
				val answer = DerivativeQuestionAnswers.create(question, answerStr) // TODO check for failure here
				Redirect(routes.Application.selfQuizAnswer(question.id, answer.id))
			})
	}

}

object EquationHTML {
	val equation = "equation"
	val form = Form(equation -> nonEmptyText)
}

object DerivativeQuestionHTML {
	val equation = "equation"
	val form = Form(equation -> nonEmptyText)
}

object DerivativeQuestionAnswerHTML {
	val questionId = "questionId"
	val answer = "answer"
	val form = Form(tuple(questionId -> number, answer -> nonEmptyText))
}
