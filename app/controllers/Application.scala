package controllers

import play.api._
import play.api.mvc._
import securesocial.core.{ Identity, Authorization }

import play.api._
import play.api.mvc._

import play.api.libs.json._
import play.api.libs.iteratee._

import models._

import akka.actor._
import scala.concurrent.duration._

object Application extends Controller with securesocial.core.SecureSocial {

	//	def index = SecuredAction { implicit request =>
	//		Ok(views.html.index("Your new application is ready."))
	//	}

	/**
	 * Just display the home page.
	 */
	def index = Action { implicit request =>
		Ok(views.html.index())
	}

	/**
	 * Display the chat room page.
	 */
	def chatRoom(username: Option[String]) = Action { implicit request =>
		username.filterNot(_.isEmpty).map { username =>
			Ok(views.html.chatRoom(username))
		}.getOrElse {
			Redirect(routes.Application.index).flashing(
				"error" -> "Please choose a valid username.")
		}
	}

	/**
	 * Handles the chat websocket.
	 */
	def chat(username: String) = WebSocket.async[JsValue] { request =>
		ChatRoom.join(username)
	}

}