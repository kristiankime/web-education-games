package controllers

import play.api._
import play.api.mvc._
import securesocial.core.{ Identity, Authorization }

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

object Application extends Controller with securesocial.core.SecureSocial {

	//	def index = SecuredAction { implicit request =>
	//		Ok(views.html.index("Your new application is ready."))
	//	}

	def index = Action {
		Redirect(routes.Application.equations)
	}

	def equations = Action {
		Ok(views.html.index(Equations.all(), EquationHTML.form))
	}

	def newEquation = Action { implicit request =>
		EquationHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.index(Equations.all(), errors)),
			equation => {
				Equations.create(equation)
				Redirect(routes.Application.equations)
			})
	}

	def deleteEquation(id: Int) = Action {
		Equations.delete(id);
		Ok(views.html.index(Equations.all(), EquationHTML.form))
	}

}

object EquationHTML {
	val equation = "equation"
	val form = Form(equation -> nonEmptyText)
}