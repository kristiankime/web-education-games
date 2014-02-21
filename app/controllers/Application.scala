package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import service.User
import models.organization.Courses
import controllers.support.SecureSocialDB

object Application extends Controller with SecureSocialDB {

	/**
	 * Application does not use trailing slashes so indicate to browsers
	 */
	def untrail(path: String) = Action {
		MovedPermanently("/" + path)
	}

	def index = SecuredUserAction { implicit request => implicit user =>
		Ok(views.html.index())
	}

	def userInfo = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		val courses = Courses.findByUser(user.id)
		Ok(views.html.user.userInfo(courses))
	}
}
