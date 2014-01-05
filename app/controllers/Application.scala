package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import securesocial.core.SecureSocial


object Application extends Controller with SecureSocial {

	/**
	 * Application does not use trailing slashes so indicate to browsers
	 */
	def untrail(path: String) = Action {
		MovedPermanently("/" + path)
	}

	def index = SecuredAction {
		Ok(views.html.index())
	}

}
