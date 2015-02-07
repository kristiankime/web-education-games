package controllers.support


import play.api.Play.current
import play.api.db.slick.Config.driver.simple.Session
import play.api.db.slick.DB
import play.api.mvc.{AnyContent, Result}
import securesocial.core.{Authorization, SecureSocial, SecuredRequest}
import service.Login

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
		def apply(f: SecuredRequest[AnyContent] => Login => Session => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
			DB.withSession { session: Session =>
				f(request)(Login(request))(session)
			}
		}

		// LATER this method is essentially the same as the one above and exists for Intellij 14 IDE help
		def apply(dummy: String)(f: SecuredRequest[AnyContent] => Login => Session => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
			DB.withSession { session: Session =>
				f(request)(Login(request))(session)
			}
		}

		def apply(authorize: Authorization)(f: SecuredRequest[AnyContent] => Login => Session => Result) = SecuredAction(authorize) { request: SecuredRequest[AnyContent] =>
			DB.withSession { session: Session =>
				f(request)(Login(request))(session)
			}
		}
		
		def apply(ajaxCall: Boolean)(f: SecuredRequest[AnyContent] => Login => Session => Result) = SecuredAction(ajaxCall) { request: SecuredRequest[AnyContent] =>
			DB.withSession { session: Session =>
				f(request)(Login(request))(session)
			}
		}

		def apply(ajaxCall: Boolean, authorize: Authorization)(f: SecuredRequest[AnyContent] => Login => Session => Result) = SecuredAction(ajaxCall, authorize) { request: SecuredRequest[AnyContent] =>
			DB.withSession { session: Session =>
				f(request)(Login(request))(session)
			}
		}
		
	}

}