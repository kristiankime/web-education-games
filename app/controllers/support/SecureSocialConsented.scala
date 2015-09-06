package controllers.support

import controllers.routes
import models.user._
import play.api.Play.current
import play.api.db.slick.Config.driver.simple.Session
import play.api.db.slick.DB
import play.api.mvc.{AnyContent, Result}
import securesocial.core.{Authorization, SecureSocial, SecuredRequest}
import service.Login

trait SecureSocialConsented extends SecureSocial {

  object ConsentedAction {

    private def consentForm(path: String) : Result = // LATER Type indicated for Intellij 14 IDE help
      Redirect(routes.Consent.consent(Some(path), None))

    private def run(request: SecuredRequest[AnyContent], f: SecuredRequest[AnyContent] => User => Session => Result)(implicit session: Session) = {
      val login = Login(request)
      Users(login.id) match {
        case None => consentForm(request.path)
        case Some(setting) => {
            if(!setting.consented) { consentForm(request.path) }
            else { f(request)(setting)(session) }
        }
      }
    }

    def apply(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
      DB.withSession { implicit session: Session =>
        run(request, f)
      }
    }

    // LATER this method is essentially the same as the one above and exists for Intellij 14 IDE help
    def apply(dummy: String)(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
      DB.withSession { implicit session: Session =>
        run(request, f)
      }
    }

    def apply(authorize: Authorization)(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction(authorize) { request: SecuredRequest[AnyContent] =>
      DB.withSession { implicit session: Session =>
        run(request, f)
      }
    }

    def apply(ajaxCall: Boolean)(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction(ajaxCall) { request: SecuredRequest[AnyContent] =>
      DB.withSession { implicit session: Session =>
        run(request, f)
      }
    }

    def apply(ajaxCall: Boolean, authorize: Authorization)(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction(ajaxCall, authorize) { request: SecuredRequest[AnyContent] =>
      DB.withSession { implicit session: Session =>
        run(request, f)
      }
    }

  }
}