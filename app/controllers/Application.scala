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
//		Redirect(routes.Application.equations)
		Ok(views.html.index())
	}

	// Equations
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

	
	// Derivative Questions
	def derivativeq = Action {
		Ok(views.html.derivativeq(DerivativeQuestions.all(), DerivativeQHTML.form))
	}

	def newDerivativeq = Action { implicit request =>
		EquationHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.derivativeq(DerivativeQuestions.all(), errors)),
			equation => {
				Equations.create(equation)
				Redirect(routes.Application.equations)
			})
	}

	def deleteDerivativeq(id: Int) = Action {
		DerivativeQuestions.delete(id);
		Ok(views.html.derivativeq(DerivativeQuestions.all(), DerivativeQHTML.form))
	}

}

object EquationHTML {
	val equation = "equation"
	val form = Form(equation -> nonEmptyText)
}

object DerivativeQHTML {
	val equation = "equation"
	val form = Form(equation -> nonEmptyText)
}