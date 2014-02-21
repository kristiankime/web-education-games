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

	object SecuredDBAction {
		def apply(f: (SecuredRequest[AnyContent], Session) => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
			DB.withSession { session: Session =>
				f(request, session)
			}
		}

		def apply(f: (SecuredRequest[AnyContent], Session, User) => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
			DB.withSession { session: Session =>
				val user = User(request)
				f(request, session, user)
			}
		}
	}

}