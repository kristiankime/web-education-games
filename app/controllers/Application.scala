package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import service.User

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

	def userInfo = SecuredAction { implicit request =>
		request.user match {
			case user: User => {
				Ok(views.html.user.userInfo(user))
			}
			case _ => throw new IllegalStateException("User was not the expected type this should not happen") // TODO better handling then just throwing 
		}

	}

}
