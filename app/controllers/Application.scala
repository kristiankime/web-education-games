package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.data._
import play.api.data.Forms._
import akka.actor._
import scala.concurrent.duration._
import models._
import mathml._
import scala.util._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import scala.slick.session.Session

//===
import play.api.Logger
import play.api.Play.current

object Application extends Controller with securesocial.core.SecureSocial {

	/**
	 * Application does not use trailing slashes so indicate to browsers
	 */
	def untrail(path: String) = Action {
		MovedPermanently("/" + path)
	}

	def index = SecuredAction {
		Ok(views.html.index())
	}

}
