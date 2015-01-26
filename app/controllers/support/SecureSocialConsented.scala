package controllers.support

import controllers.routes
import play.api.db.slick.Config.driver.simple.Session
import play.api.Play.current
import play.api.db.slick.DB
import play.api.mvc.{Results, AnyContent, Result}
import securesocial.core.SecureSocial
import securesocial.core.SecuredRequest
import securesocial.core.Authorization
import service.Login
import models.user._

trait SecureSocialConsented extends SecureSocial {

  object ConsentedAction {

    def consentForm(path: String) : Result = // LATER Type indicated for Intellij 14 IDE help
      Redirect(routes.Consent.consent(Some(path), None))

    def apply(f: SecuredRequest[AnyContent] => UserFull => Session => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
      DB.withSession { implicit session: Session =>
        val user = Login(request)
        if(!user.consented) consentForm(request.path)
        else f(request)(UserFull(user))(session)
      }
    }

    // LATER this method is essentially the same as the one above and exists for Intellij 14 IDE help
    def apply(dummy: String)(f: SecuredRequest[AnyContent] => UserFull => Session => Result) = SecuredAction { request: SecuredRequest[AnyContent] =>
      DB.withSession { implicit session: Session =>
        val user = Login(request)
        if(!user.consented) consentForm(request.path)
        else f(request)(UserFull(user))(session)
      }
    }

    def apply(authorize: Authorization)(f: SecuredRequest[AnyContent] => UserFull => Session => Result) = SecuredAction(authorize) { request: SecuredRequest[AnyContent] =>
      DB.withSession { implicit session: Session =>
        val user = Login(request)
        if(!user.consented) consentForm(request.path)
        else f(request)(UserFull(user))(session)
      }
    }

    def apply(ajaxCall: Boolean)(f: SecuredRequest[AnyContent] => UserFull => Session => Result) = SecuredAction(ajaxCall) { request: SecuredRequest[AnyContent] =>
      DB.withSession { implicit session: Session =>
        val user = Login(request)
        if(!user.consented) consentForm(request.path)
        else f(request)(UserFull(user))(session)
      }
    }

    def apply(ajaxCall: Boolean, authorize: Authorization)(f: SecuredRequest[AnyContent] => UserFull => Session => Result) = SecuredAction(ajaxCall, authorize) { request: SecuredRequest[AnyContent] =>
      DB.withSession { implicit session: Session =>
        val user = Login(request)
        if(!user.consented) consentForm(request.path)
        else f(request)(UserFull(user))(session)
      }
    }

  }
}