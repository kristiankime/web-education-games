package controllers.tournament

import controllers.support.SecureSocialConsented
import models.organization._
import models.support._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc._

object TournamentsController extends Controller with SecureSocialConsented {

  def leaderBoard = ConsentedAction("TODO remove when intellij can parse without this") { implicit request => implicit user => implicit session =>
    Ok(views.html.tournament.leaderBoard())
  }

}