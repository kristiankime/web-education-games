package controllers.game

import controllers.game.GamesController._
import controllers.support.SecureSocialConsented
import models.game._
import models.organization._
import play.api.mvc.Controller
import service.User
import play.api.db.slick.Config.driver.simple.Session

object GamesRequesteeController extends Controller with SecureSocialConsented {

   def requesteeGameViews(organization: Organization, course: Course, game: Game)(implicit user: User, session: Session) = {
     game.toState match {
       case gameState: GameState with RequesteeQuizUnfinished => Ok(views.html.game.createQuizRequestee(organization, course, gameState))
       case gameState: GameState with GameRequested => Ok(views.html.game.responedToGameRequest(organization, course, gameState))
       case _ =>  throw new IllegalStateException("Not tee state mach, TODO this should be removeable via sealed")
     }
   }

 }
