package controllers

import controllers.Application._
import controllers.support.{SecureSocialConsented, SecureSocialDB}
import play.api.mvc.Controller

object Home extends Controller with SecureSocialConsented {

  def index = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.index())
  }

  def userInfo = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.user.userInfo())
  }

}
