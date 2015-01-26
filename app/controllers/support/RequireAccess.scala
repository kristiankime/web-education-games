package controllers.support

import play.api.db.slick.Config.driver.simple.Session
import securesocial.core.Authorization
import securesocial.core.Identity
import play.api.db.slick.DB
import play.api.Play.current
import service._
import models.support._
import models.organization._

case class RequireAccess(level: Access, secured: Session => Option[Secured]) extends Authorization {

	def isAuthorized(user: Identity) = DB.withSession { session: Session =>
		(user, secured(session)) match {
			case (u: Login, Some(s)) => s.access(u, session) >= level
			case (u: Login, None) => false
			case _ => throw new IllegalStateException("User was not the expected type this should not happen, programatic error")
		}
	}

}

object RequireAccess {
	
	def apply(courseId: CourseId) = new RequireAccess(View, (s:Session) => Courses(courseId)(s))

	def apply(level: Access, courseId: CourseId) = new RequireAccess(level, (s:Session) => Courses(courseId)(s))

}


