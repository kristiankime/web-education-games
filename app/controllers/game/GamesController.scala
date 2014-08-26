package controllers.game

import com.artclod.util._
import controllers.organization.CoursesController
import controllers.support.SecureSocialConsented
import models.game._
import models.organization._
import models.support._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc._

import scala.util.Right

object GamesController extends Controller with SecureSocialConsented {

  def apply(organizationId: OrganizationId, courseId: CourseId, gameId: GameId)(implicit session: Session): Either[Result, (Organization, Course, Game)] =
    Games(gameId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no course for id=[" + courseId + "]")))
      case Some(game) => game.courseId match {
        case None => Left(NotFound(views.html.errors.notFoundPage("The game with id=[" + gameId + "] is not associated with any course (including [" + courseId + "]")))
        case Some(gameId) => CoursesController(organizationId, courseId) + Right(game)
      }
    }

  def findPlayer(organizationId: OrganizationId, courseId: CourseId) = ConsentedAction { implicit request => implicit user => implicit session =>
    CoursesController(organizationId, courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course)) => Ok(views.html.game.findPlayer(organization, course))
    }
  }

  def requestGame(organizationId: OrganizationId, courseId: CourseId) = ConsentedAction { implicit request => implicit user => implicit session =>
    CoursesController(organizationId, courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course)) => GameRequest.form.bindFromRequest.fold(
        errors => BadRequest(views.html.errors.formErrorPage(errors)),
        form => {
          val otherUserId = form
          if (Games.activeGame(user.id, otherUserId).nonEmpty) {
            BadRequest(views.html.errors.errorPage(new IllegalStateException("Users already had an active game [" + user.id + "] [" + otherUserId + "]")))
          } else {
            Games.request(user.id, otherUserId, course.id)
            Redirect(controllers.organization.routes.CoursesController.view(organization.id, course.id))
          }
        })
    }
  }

  def game(organizationId: OrganizationId, courseId: CourseId, gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(organizationId, courseId, gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, game)) =>
        // -------------------------------------------------------------------------------------
        if(game.isRequestor(user)) game.toState match {
          case state: RequestorQuiz => Ok(views.html.game.createQuizRequestor(organization, course, state))
          case state: RequestorQuizFinished with RequesteeQuiz => Ok(views.html.game.awaitingQuizRequestor(organization, course, state))
          case state: RequestorQuizFinished with RequesteeQuizFinished => Ok(views.html.game.answeringQuizRequestor(organization, course, state))
          case _ =>  throw new IllegalStateException("Not tor state mach, TODO this should be removeable via sealed")
        }
        else if(game.isRequestee(user)) game.toState match {
          case state: GameRequested => Ok(views.html.game.responedToGameRequest(organization, course, state))
          case state: RequesteeQuiz => Ok(views.html.game.createQuizRequestee(organization, course, state))
          case state: RequesteeQuizFinished with RequestorQuiz => Ok(views.html.game.awaitingQuizRequestee(organization, course, state))
          case state: RequestorQuizFinished with RequesteeQuizFinished => Ok(views.html.game.answeringQuizRequestee(organization, course, state))
          case _ =>  throw new IllegalStateException("Not tee state mach, TODO this should be removeable via sealed")
        }
        else throw new IllegalStateException("TODO code up teacher view")
      // ---------------------------------------------------------------------------------------
      }
    }

  def respond(organizationId: OrganizationId, courseId: CourseId, gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(organizationId, courseId, gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, game)) => GameResponse.form.bindFromRequest.fold(
        errors => BadRequest(views.html.errors.formErrorPage(errors)),
        accepted => {
          val gameState = game.toState match {
            case g : GameRequested => g
            case _ =>  throw new IllegalStateException("State should have been subclass of [" + classOf[GameRequested].getName + "] but was " + game.toState)
          }
          if (accepted) { Games.update(gameState.accept(user.id)) }
          else { Games.update(gameState.reject(user.id)) }
          Redirect(routes.GamesController.game(organization.id, course.id, game.id))
        })
    }
  }

}

object GameRequest {
  val requestee = "requestee"
  val form = Form(requestee -> userId)
}

object GameResponse {
  val response = "reponse"
  val form = Form(response -> boolean)
}