package controllers

import scala.slick.session.Session

import models.EquationsModel
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms.nonEmptyText
import play.api.db.slick.DB
import play.api.mvc.Controller
import securesocial.core.SecureSocial

object EquationController extends Controller with SecureSocial {

	def equations = SecuredAction {
		DB.withSession { implicit session: Session =>
			Ok(views.html.equations(EquationsModel.all(), EquationHTML.form))
		}
	}

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

	def deleteEquation(id: Long) = SecuredAction { implicit request =>
		DB.withSession { implicit session: Session =>
			EquationsModel.delete(id)
			Ok(views.html.equations(EquationsModel.all(), EquationHTML.form))
		}
	}

}

object EquationHTML {
	val equation = "equation"
	val form = Form(equation -> nonEmptyText)
}
