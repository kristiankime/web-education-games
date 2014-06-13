package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import models.organization.Courses
import controllers.support.SecureSocialDB

object Application extends Controller with SecureSocialDB {
	val version = Version(0, 3, 2)
	
	/**
	 * Application does not use trailing slashes so indicate to browsers
	 */
	def untrail(path: String) = Action {
		MovedPermanently("/" + path)
	}

  def backTrack(path: String) = Action {
    Redirect("/" + path.substring(0, path.lastIndexOf("/")))
  }

	def index = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Ok(views.html.index())
	}

	def userInfo = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Ok(views.html.user.userInfo())
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
