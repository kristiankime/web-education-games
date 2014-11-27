package controllers.game

import com.artclod.play.CommonsMailerHelper
import models.game.Game
import play.api.mvc.Request

object GamesEmail {

  def gameLinkEmail(request: Request[_], game: Game, linkText: String) = "<a href=\"" + CommonsMailerHelper.serverAddress(request) + controllers.game.routes.GamesController.game(game.id, None) + "\">" + linkText + "</a>"

  def serverLinkEmail(request: Request[_]) = "<a href=\"" + CommonsMailerHelper.serverAddress(request) + "\">CalcTutor</a>"

}
