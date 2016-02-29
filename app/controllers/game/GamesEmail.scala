package controllers.game

import com.artclod.play.CommonsMailerHelper
import models.game.Game
import play.api.mvc.Request

object GamesEmail {

  def goToGameLinkEmail(request: Request[_], game: Game) =  gameLinkEmail(request, game, "go to the game")

  private def gameLinkEmail(request: Request[_], game: Game, linkText: String) = "<a href=\"" + CommonsMailerHelper.serverAddress(request) + controllers.game.routes.GamesController.game(game.id, None) + "\">" + linkText + "</a>"

  def serverLinkEmail(request: Request[_]) = "<a href=\"" + CommonsMailerHelper.serverAddress(request) + "\">CalcTutor</a>"

  def goToFriendRequestLinkEmail(request: Request[_]) =  friendRequestLinkEmail(request, "go to the friend request")

  private def friendRequestLinkEmail(request: Request[_], linkText: String) = "<a href=\"" + CommonsMailerHelper.serverAddress(request) + controllers.routes.Home.friends() + "\">" + linkText + "</a>"

}
