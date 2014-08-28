package controllers.game

import com.artclod.util._
import controllers.organization.CoursesController
import controllers.support.SecureSocialConsented
import models.game._
import models.organization._
import models.question.derivative.{Quiz, Question}
import models.support._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc._

import scala.util.Right

object GamesController extends Controller with SecureSocialConsented {

  def apply(organizationId: OrganizationId, courseId: CourseId, gameId: GameId)(implicit session: Session): Either[Result, (Organization, Course, Game)] =
    Games(gameId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no gameId for id=[" + gameId + "]")))
      case Some(game) => game.courseId match {
        case None => Left(NotFound(views.html.errors.notFoundPage("The game with id=[" + gameId + "] is not associated with any course (including [" + courseId + "]")))
        case Some(`courseId`) => CoursesController(organizationId, courseId) + Right(game)
        case Some(otherCourseId) => Left(NotFound(views.html.errors.notFoundPage("The game with id=[" + gameId + "] was for course[" + courseId + "] not for [" + otherCourseId + "]")))
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
          case state: RequestorDoneAnswering => Ok(views.html.game.gameDoneRequestor(organization, course, state))
          case state: RequestorQuiz => Ok(views.html.game.createQuizRequestor(organization, course, state))
          case state: RequestorQuizFinished with RequesteeQuiz => Ok(views.html.game.awaitingQuizRequestor(organization, course, state))
          case state: RequestorQuizFinished with RequesteeQuizFinished => Ok(views.html.game.answeringQuizRequestor(organization, course, state))
          case _ =>  throw new IllegalStateException("No match in Requestor State, programming error")
        }
        else if(game.isRequestee(user)) game.toState match {
          case state: RequesteeDoneAnswering => Ok(views.html.game.gameDoneRequestee(organization, course, state))
          case state: GameRequested => Ok(views.html.game.responedToGameRequest(organization, course, state))
          case state: RequesteeQuiz => Ok(views.html.game.createQuizRequestee(organization, course, state))
          case state: RequesteeQuizFinished with RequestorQuiz => Ok(views.html.game.awaitingQuizRequestee(organization, course, state))
          case state: RequestorQuizFinished with RequesteeQuizFinished => Ok(views.html.game.answeringQuizRequestee(organization, course, state))
          case _ =>  throw new IllegalStateException("No match in Requestee State, programming error")
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

  def question(organizationId: OrganizationId, courseId: CourseId, gameId: GameId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(organizationId, courseId, gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, game)) => {

        if(game.isRequestor(user))
          GamesRequesteeController(gameId, questionId) match { // Use GamesRequesteeController here to get requestee quiz
            case Left(notFoundResult) => notFoundResult
            case Right((game, quiz, question)) => Ok(views.html.game.answeringQuestionRequestor(organization, course, quiz, question, None))
          }

        else if(game.isRequestee(user))
          GamesRequestorController(gameId, questionId) match { // Use GamesRequestorController here to get requestor quiz
            case Left(notFoundResult) => notFoundResult
            case Right((game, quiz, question)) => Ok(views.html.game.answeringQuestionRequestee(organization, course, quiz, question, None))
          }
        else throw new IllegalStateException("user was not requestee or requestor user = [" + user + "] game = [" + game + "]")
      }
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