package controllers

import play.api.mvc.Action
import play.api.mvc.Controller

object Application extends Controller {
	val version = Version(0, 4, 0)
	
	/**
	 * Application does not use trailing slashes so indicate to browsers
	 */
	def untrail(path: String) = Action {
		MovedPermanently("/" + path)
	}

  /**
   * If the path is not valid backtrack to an earlier "/"
   */
  def backTrack(path: String) = Action {
    Redirect(("/" + path).substring(0, ("/" + path).lastIndexOf("/") + 1))
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
