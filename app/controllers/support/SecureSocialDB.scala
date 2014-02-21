package controllers.support

import scala.slick.session.Session

import play.api.Play.current
import play.api.db.slick.DB
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import securesocial.core.SecureSocial
import securesocial.core.SecuredRequest
import service.User

trait SecureSocialDB extends SecureSocial {

	object SecuredUserAction {

		def apply(f: SecuredRequest[AnyContent] => User => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
			val user = User(request)
			f(request)(user)
		}
	}

	object SecuredDBAction {
		def apply(f: SecuredRequest[AnyContent] => Session => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
			DB.withSession { session: Session =>
				f(request)(session)
			}
		}
	}

	object SecuredUserDBAction {
		def apply(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
			val user = User(request)
			DB.withSession { session: Session =>
				f(request)(user)(session)
			}
		}
	}

}