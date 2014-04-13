package controllers

import models.EquationsModel
import play.api.data.Form
import play.api.data.Forms.nonEmptyText
import play.api.mvc.Controller
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
