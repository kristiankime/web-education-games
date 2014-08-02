package controllers.support


import play.api.db.slick.Config.driver.simple.Session
import play.api.db.slick.DB
import play.api.mvc.AnyContent
import play.api.mvc.Result
import play.api.Play.current
import securesocial.core.SecureSocial
import securesocial.core.SecuredRequest
import securesocial.core.Authorization
import service.User

trait SecureSocialDB extends SecureSocial {

//	object SecuredUserAction {
//		def apply(f: SecuredRequest[AnyContent] => User => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
//			val user = User(request)
//			f(request)(user)
//		}
//	}
//
//	object SecuredDBAction {
//		def apply(f: SecuredRequest[AnyContent] => Session => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
//			DB.withSession { session: Session =>
//				f(request)(session)
//			}
//		}
//	}

	object SecuredUserDBAction {
		def apply(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
			DB.withSession { session: Session =>
				f(request)(User(request))(session)
			}
		}

		def apply(authorize: Authorization)(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction(authorize) { request: SecuredRequest[AnyContent] =>
			DB.withSession { session: Session =>
				f(request)(User(request))(session)
			}
		}
		
		def apply(ajaxCall: Boolean)(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction(ajaxCall) { request: SecuredRequest[AnyContent] =>
			DB.withSession { session: Session =>
				f(request)(User(request))(session)
			}
		}

		def apply(ajaxCall: Boolean, authorize: Authorization)(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction(ajaxCall, authorize) { request: SecuredRequest[AnyContent] =>
			DB.withSession { session: Session =>
				f(request)(User(request))(session)
			}
		}
		
	}

}