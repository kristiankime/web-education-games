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

object EquationController extends Controller with securesocial.core.SecureSocial {

	def equations = SecuredAction {
		DB.withSession { implicit session: Session =>
			Ok(views.html.equations(EquationsModel.all(), EquationHTML.form))
		}
	}

	//	def equations = DBAction { implicit dbSessionRequest =>
	//		Ok(views.html.equations(EquationsModel.all(), EquationHTML.form))
	//	}

	def newEquation = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			EquationHTML.form.bindFromRequest.fold(
				errors => BadRequest(views.html.equations(EquationsModel.all(), errors)),
				equation => {
					EquationsModel.create(equation)
					Redirect(routes.EquationController.equations)
				})
		}
	}

	//	def newEquation = DBAction { implicit dbSessionRequest =>
	//		EquationHTML.form.bindFromRequest.fold(
	//			errors => BadRequest(views.html.equations(EquationsModel.all(), errors)),
	//			equation => {
	//				EquationsModel.create(equation)
	//				Redirect(routes.EquationController.equations)
	//			})
	//	}

	def deleteEquation(id: Long) = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			EquationsModel.delete(id)
			Ok(views.html.equations(EquationsModel.all(), EquationHTML.form))
		}
	}

	//	def deleteEquation(id: Long) = DBAction { implicit dbSessionRequest =>
	//		EquationsModel.delete(id)
	//		Ok(views.html.equations(EquationsModel.all(), EquationHTML.form))
	//	}

}

object EquationHTML {
	val equation = "equation"
	val form = Form(equation -> nonEmptyText)
}
