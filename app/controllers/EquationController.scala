package controllers

import models.Equations
import play.api.data.Form
import play.api.data.Forms.nonEmptyText
import play.api.mvc.Controller
import controllers.support.SecureSocialDB

object EquationController extends Controller with SecureSocialDB {

	def equations = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Ok(views.html.equations(Equations.all(), EquationHTML.form))
	}

	def newEquation = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		EquationHTML.form.bindFromRequest.fold(
			errors => BadRequest(views.html.equations(Equations.all(), errors)),
			equation => {
				Equations.create(equation)
				Redirect(routes.EquationController.equations)
			})
	}

	def deleteEquation(id: Long) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Equations.delete(id)
		Ok(views.html.equations(Equations.all(), EquationHTML.form))
	}

}

object EquationHTML {
	val equation = "equation"
	val form = Form(equation -> nonEmptyText)
}
