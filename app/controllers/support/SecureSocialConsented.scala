package controllers.support

import controllers.routes
import models.user._
import play.api.Play.current
import play.api.db.slick.Config.driver.simple.Session
import play.api.db.slick.DB
import play.api.mvc.{AnyContent, Result}
import securesocial.core.{Authorization, SecureSocial, SecuredRequest}
import service.Login

import scala.util.{Success, Failure}

trait SecureSocialConsented extends SecureSocial {

  object ConsentedAction {

    private def consentForm(path: String) : Result = // LATER Type indicated for Intellij 14 IDE help
      Redirect(routes.Consent.consent(Some(path), None))

    private def run(request: SecuredRequest[AnyContent], f: SecuredRequest[AnyContent] => User => Session => Result)(implicit session: Session) = {
      val login = Login(request)
      Users.access(login.id) match {
        case Failure(_) => consentForm(request.path)
        case Success(user) => {
            if(!user.consented) { consentForm(request.path) }
            else { f(request)(user)(session) }
        }
      }
    }

    def apply(f: SecuredRequest[AnyContent] => User => Session => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
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