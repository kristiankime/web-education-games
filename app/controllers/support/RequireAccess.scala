package controllers.support

import models.organization._
import models.support._
import models.user.Users
import play.api.Play.current
import play.api.db.slick.Config.driver.simple.Session
import play.api.db.slick.DB
import securesocial.core.{Authorization, Identity}
import service._

case class RequireAccess(level: Access, secured: Session => Option[Secured]) extends Authorization {

	def isAuthorized(identity: Identity) = DB.withSession { implicit session: Session =>
		(identity, secured(session)) match {
			case (login: Login, Some(s)) =>
				Users(login.id) match {
					case None => false
					case Some(user) => s.access(user, session) >= level
				}
			case (login: Login, None) => false
			case _ => throw new IllegalStateException("Identity was not the expected type this should not happen, programatic error")
		}
	}

}

object RequireAccess {
	
	def apply(courseId: CourseId) = new RequireAccess(View, (s:Session) => Courses(courseId)(s))

	def apply(level: Access, courseId: CourseId) = new RequireAccess(level, (s:Session) => Courses(courseId)(s))

}


