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

	
	// ======== Self Quiz Actions ======== 
	def selfQuizQuestions = Action {
		Ok(views.html.self_quiz_list(DerivativeQuestions.all(), DerivativeQuestionHTML.form))
	}

	def newSelfQuizQuestion = Action { implicit request =>
		EquationHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.self_quiz_list(DerivativeQuestions.all(), errors)),
			equation => {
				Equations.create(equation)
				Redirect(routes.Application.selfQuizQuestions)
			})
	}

	def deleteSelfQuizQuestion(id: Int) = Action {
		DerivativeQuestions.delete(id);
		Ok(views.html.self_quiz_list(DerivativeQuestions.all(), DerivativeQuestionHTML.form))
	}
	
	def takeSelfQuizQuestion(id: Int) = Action {
		Ok(views.html.self_quiz_take(DerivativeQuestions.get(id)))
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