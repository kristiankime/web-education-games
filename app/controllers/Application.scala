package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import service.User
import models.organization.Courses

object Application extends Controller with SecureSocial {

	/**
	 * Application does not use trailing slashes so indicate to browsers
	 */
	def untrail(path: String) = Action {
		MovedPermanently("/" + path)
	}

	def index = SecuredAction  { implicit request =>
		implicit val user = User(request)
		Ok(views.html.index())
	}

	def userInfo = SecuredAction { implicit request =>
		implicit val user = User(request)
		val courses = Courses.findByUser(user.id)
		Ok(views.html.user.userInfo(courses))
	}

	def xslttest = Action {
		Ok(views.html.xslttest())
	}
	
}
