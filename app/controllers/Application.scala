package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import models.organization.Courses
import controllers.support.SecureSocialDB

object Application extends Controller with SecureSocialDB {
	val version = Version(0, 2, 9)
	
	/**
	 * Application does not use trailing slashes so indicate to browsers
	 */
	def untrail(path: String) = Action {
		MovedPermanently("/" + path)
	}

	def index = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Ok(views.html.index())
	}

	def userInfo = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		val courses = Courses(user.id)
		Ok(views.html.user.userInfo(courses))
	}
}

object Version{
	def apply(major: Int, minor: Int) : Version = Version(major, minor, None)

	def apply(major: Int, minor: Int, build: Int) : Version = Version(major, minor, Some(build))
}

case class Version(major: Int, minor: Int, build: Option[Int]){
	override def toString = "v" + major + "." + minor + (build match {
		case None => ""
		case Some(b) => "." + b
	})
}
