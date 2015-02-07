package controllers

import controllers.support.SecureSocialConsented
import play.api.mvc.Controller

object Home extends Controller with SecureSocialConsented {

  def index = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.index())
  }

  def userInfo = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.user.userInfo())
  }

}
