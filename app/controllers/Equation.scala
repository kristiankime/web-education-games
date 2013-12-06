package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.data._
import play.api.data.Forms._
import akka.actor._
import scala.concurrent.duration._
import models._
import mathml._
import scala.util._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import scala.slick.session.Session

//===
import play.api.Logger
import play.api.Play.current

object Equation extends Controller {

	def equations = DBAction { implicit dbSessionRequest =>
		Ok(views.html.equations(EquationsModel.all(), EquationHTML.form))
	}

	def newEquation = DBAction { implicit dbSessionRequest =>
		EquationHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.equations(EquationsModel.all(), errors)),
			equation => {
				EquationsModel.create(equation)
				Redirect(routes.Equation.equations)
			})
	}

	def deleteEquation(id: Long) = DBAction { implicit dbSessionRequest =>
		EquationsModel.delete(id)
		Ok(views.html.equations(EquationsModel.all(), EquationHTML.form))
	}

}

object EquationHTML {
	val equation = "equation"
	val form = Form(equation -> nonEmptyText)
}
