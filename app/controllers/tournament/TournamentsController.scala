package controllers.tournament

import controllers.support.SecureSocialConsented
import play.api.mvc._

object TournamentsController extends Controller with SecureSocialConsented {

  def leaderBoard = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.tournament.leaderBoard())
  }

}
