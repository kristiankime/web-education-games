package controllers

import com.artclod.slick.JodaUTC
import controllers.organization.CoursesController._
import models.user.{Friend, Friends, Users}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import com.artclod.random._
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.support.SecureSocialConsented
import models.organization._
import models.support._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc.{Controller, Result}
import service._
import play.api.data.format.Formats._

object Home extends Controller with SecureSocialConsented {

  def index = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.index())
  }

  def userInfo = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.user.userInfo())
  }

  def friends = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.user.friends())
  }

  def friendRequest = ConsentedAction { implicit request => implicit user => implicit session =>
    FriendRequest.form.bindFromRequest.fold(
      errors => BadRequest(views.html.errors.formErrorPage(errors)),
      form => {
        val friendId = UserId(form)
        Users(friendId) match {
          case None => NotFound(views.html.errors.notFoundPage("There was no user for id=[" + friendId + "]"))
          case Some(friend) => {
            Friends.invitation(friendId)
            Redirect(routes.Home.friends())
          }
        }
      })
  }

  def friendResponse = ConsentedAction { implicit request => implicit user => implicit session =>
    FriendResponse.form.bindFromRequest.fold(
      errors => BadRequest(views.html.errors.formErrorPage(errors)),
      form => {
        val requestorId = UserId(form._1)
        Friends(requestorId, user.id)(session) match {
          case None => NotFound(views.html.errors.notFoundPage("There was no request for id=[" + requestorId + " & " + user.id + "]"))
          case Some(friend) => {
            if(form._2) { Friends.acceptInvitation(friend) } else { Friends.rejectInvitation(friend) }
            Redirect(routes.Home.friends())
          }
        }
      })
  }

}

object FriendRequest {
  val id = "id"
  val form = Form(id -> of[Long])
}

object FriendResponse {
  val id = "id"
  val accept = "accept"
  val form = Form(tuple(id -> of[Long], accept -> boolean))
}