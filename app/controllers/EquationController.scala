package controllers

import scala.slick.session.Session
import models.EquationsModel
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms.nonEmptyText
import play.api.db.slick.DB
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import service.User
import controllers.support.SecureSocialDB

object EquationController extends Controller with SecureSocialDB {

	def equations = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Ok(views.html.equations(EquationsModel.all(), EquationHTML.form))
	}

	def newEquation = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		EquationHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.equations(EquationsModel.all(), errors)),
			equation => {
				EquationsModel.create(equation)
				Redirect(routes.EquationController.equations)
			})
	}

	def deleteEquation(id: Long) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		EquationsModel.delete(id)
		Ok(views.html.equations(EquationsModel.all(), EquationHTML.form))
	}

}

object EquationHTML {
	val equation = "equation"
	val form = Form(equation -> nonEmptyText)
}
