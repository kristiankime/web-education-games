package controllers.support

import controllers.routes
import play.api.db.slick.Config.driver.simple.Session
import play.api.Play.current
import play.api.db.slick.DB
import play.api.mvc.AnyContent
import play.api.mvc.Result
import securesocial.core.SecureSocial
import securesocial.core.SecuredRequest
import securesocial.core.Authorization
import service.User
import models.user._

trait SecureSocialConsented extends SecureSocial {


  object ConsentedAction {

    def consentForm(path: String) =
      Redirect(routes.Consent.consent(Some(path), None))

    def apply(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
      DB.withSession { session: Session =>
        val user = User(request)
        if(!user.consented(session)) consentForm(request.path)
        else f(request)(user)(session)
      }
    }


    def apply(authorize: Authorization)(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction(authorize) { request: SecuredRequest[AnyContent] =>
      DB.withSession { session: Session =>
        val user = User(request)
        if(!user.consented(session)) consentForm(request.path)
        else f(request)(user)(session)
      }
    }

    def apply(ajaxCall: Boolean)(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction(ajaxCall) { request: SecuredRequest[AnyContent] =>
      DB.withSession { session: Session =>
        val user = User(request)
        if(!user.consented(session)) consentForm(request.path)
        else f(request)(user)(session)
      }
    }

    def apply(ajaxCall: Boolean, authorize: Authorization)(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction(ajaxCall, authorize) { request: SecuredRequest[AnyContent] =>
      DB.withSession { session: Session =>
        val user = User(request)
        if(!user.consented(session)) consentForm(request.path)
        else f(request)(user)(session)
      }
    }

  }
}