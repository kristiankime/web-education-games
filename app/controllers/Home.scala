package controllers

import com.artclod.play.CommonsMailerHelper
import com.artclod.slick.JodaUTC
import controllers.game.GamesEmail._
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

  def findfriend = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.user.findFriend())
  }

  def friend = ConsentedAction { implicit request => implicit user => implicit session =>
    FriendRequest.form.bindFromRequest.fold(
      errors => BadRequest(views.html.errors.formErrorPage(errors)),
      form => {
        val friendId = UserId(form)
        Users(friendId) match {
          case None => NotFound(views.html.errors.notFoundPage("Friend: there was no user for id=[" + friendId + "]"))
          case Some(friend) => {
            if(Friends.friend(friendId)) {
              for (mail <- friend.maybeSendEmail.map(mail => CommonsMailerHelper.defaultMailSetup(mail))) {
                val userName = user.nameDisplay
                mail.setSubject(userName + " has sent you a friend request in CalcTutor")
                mail.sendHtml(userName + " wants to be your friend! " + serverLinkEmail(request) + " (" + goToFriendRequestLinkEmail(request) + ").")
              }
            }
            Redirect(routes.Home.friends())
          }
        }
      })
  }

  def unfriend = ConsentedAction { implicit request => implicit user => implicit session =>
    UnfriendRequest.form.bindFromRequest.fold(
      errors => BadRequest(views.html.errors.formErrorPage(errors)),
      form => {
        val friendId = UserId(form)
        Users(friendId) match {
          case None => NotFound(views.html.errors.notFoundPage("Unfriend: there was no user for id=[" + friendId + "]"))
          case Some(friend) => {
            Friends.unfriend(friendId)
            // TODO should we notify the unfriended?
//            if() {
//              for (mail <- friend.maybeSendEmail.map(mail => CommonsMailerHelper.defaultMailSetup(mail))) {
//                val userName = user.nameDisplay
//                mail.setSubject(userName + " has sent you a friend request in CalcTutor")
//                mail.sendHtml(userName + " wants to be your friend! " + serverLinkEmail(request) + " (" + goToFriendRequestLinkEmail(request) + ").")
//              }
//            }
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

object UnfriendRequest {
  val id = "id"
  val form = Form(id -> of[Long])
}