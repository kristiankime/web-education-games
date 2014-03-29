package controllers.support

import securesocial.core.Authorization
import securesocial.core.Identity
import service.Access
import service.User
import models.id.Secured
import play.api.db.slick.DB
import play.api.Play.current
import scala.slick.session.Session

case class RequireAccess(level: Access, secured: Session => Option[Secured]) extends Authorization {

	def isAuthorized(user: Identity) = DB.withSession { session: Session =>
		(user, secured(session)) match {
			case (u: User, Some(s)) => s.access(u, session) >= level
			case (u: User, None) => false
			case _ => throw new IllegalStateException("User was not the expected type this should not happen, programatic error")
		}
	}

}


