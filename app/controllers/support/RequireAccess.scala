package controllers.support

import scala.slick.session.Session
import securesocial.core.Authorization
import securesocial.core.Identity
import play.api.db.slick.DB
import play.api.Play.current
import service.Access
import service.User
import service.View
import models.support.Secured
import models.support.CourseId
import models.organization.Courses

case class RequireAccess(level: Access, secured: Session => Option[Secured]) extends Authorization {

	def isAuthorized(user: Identity) = DB.withSession { session: Session =>
		(user, secured(session)) match {
			case (u: User, Some(s)) => s.access(u, session) >= level
			case (u: User, None) => false
			case _ => throw new IllegalStateException("User was not the expected type this should not happen, programatic error")
		}
	}

}

object RequireAccess {
	
	def apply(courseId: CourseId) = new RequireAccess(View, (s:Session) => Courses.find(courseId)(s))

	def apply(level: Access, courseId: CourseId) = new RequireAccess(level, (s:Session) => Courses.find(courseId)(s))

}


