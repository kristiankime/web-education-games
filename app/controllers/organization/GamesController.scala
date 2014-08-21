package controllers.organization

import com.artclod.util._
import controllers.support.SecureSocialConsented
import models.organization._
import models.support._
import play.api.data.Form
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc._
import service.User

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
            Redirect(routes.CoursesController.view(organization.id, course.id))
          }
        })
    }
  }

  def game(organizationId: OrganizationId, courseId: CourseId, gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(organizationId, courseId, gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((org, course, game)) => game.toState match { 
        case g : GameRequested => teeVsTor(user, g, Ok(views.html.game.requestedTor(org, course, g)), Ok(views.html.game.requestedTee(org, course, g)))
        case _ => throw new IllegalStateException()
      }
    }
  }

  def teeVsTor(user: User, gameState: GameState, tor: SimpleResult, tee: SimpleResult) =
    user.id match {
      case gameState.requestorId => tor
      case gameState.requesteeId => tee
      case _ => throw new IllegalStateException("user [" + user + "] was not requestee or requestor for game [" + gameState + "]")
    }

}


object GameRequest {
  val requestee = "requestee"
  val form = Form(requestee -> userId)
}
