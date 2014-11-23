package controllers.game

import com.artclod.util._
import controllers.organization.CoursesController
import controllers.question.derivative.AnswersController
import controllers.support.SecureSocialConsented
import models.game._
import models.organization._
import models.support._
import models.user.Users
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc._
import models.game.GameRole._

object GamesController extends Controller with SecureSocialConsented {

  def apply(gameId: GameId)(implicit session: Session): Either[Result, Game] =
    Games(gameId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no gameId for id=[" + gameId + "]")))
      case Some(game) => Right(game)
    }

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
      case Right((organization, course)) => Ok(views.html.game.request.findPlayer(organization, course))
    }
  }

  def requestGame(organizationId: OrganizationId, courseId: CourseId) = ConsentedAction { implicit request => implicit user => implicit session =>
    CoursesController(organizationId, courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course)) => GameRequest.form.bindFromRequest.fold(
        errors => BadRequest(views.html.errors.formErrorPage(errors)),
        otherUserId => Games.activeGame(user.id, otherUserId) match {
          case Some(game) => Redirect(controllers.game.routes.GamesController.game(game.id)) // TODO accept game if in awaiting state
          case None => {
            val game = Games.request(user, Users(otherUserId).get, course)
            Redirect(controllers.game.routes.GamesController.game(game.id))
          }
        })
    }
  }

  def game(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        if(game.isRequestor(user)) game.toState match {
          case state: GameRejected => Ok(views.html.game.request.rejectedRequestor(state))
          case state: RequestorDoneAnswering => Ok(views.html.game.play.gameDoneRequestor(state))
          case state: RequestorQuiz => Ok(views.html.game.play.createQuizRequestor(state))
          case state: RequestorQuizFinished with RequesteeQuiz => Ok(views.html.game.play.awaitingQuizRequestor(state))
          case state: RequestorQuizFinished with RequesteeQuizFinished => Ok(views.html.game.play.answeringQuizRequestor(state))
          case _ =>  throw new IllegalStateException("No match in Requestor State, programming error")
        }
        else if(game.isRequestee(user)) game.toState match {
          case state: GameRejected => Ok(views.html.game.request.rejectedRequestee(state))
          case state: RequesteeDoneAnswering => Ok(views.html.game.play.gameDoneRequestee(state))
          case state: GameRequested => Ok(views.html.game.request.responedToGameRequest(state))
          case state: RequesteeQuiz => Ok(views.html.game.play.createQuizRequestee(state))
          case state: RequesteeQuizFinished with RequestorQuiz => Ok(views.html.game.play.awaitingQuizRequestee(state))
          case state: RequestorQuizFinished with RequesteeQuizFinished => Ok(views.html.game.play.answeringQuizRequestee(state))
          case _ =>  throw new IllegalStateException("No match in Requestee State, programming error")
        }
        else throw new IllegalStateException("TODO code up teacher view")
      }
    }

  def respond(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => GameResponse.form.bindFromRequest.fold(
        errors => BadRequest(views.html.errors.formErrorPage(errors)),
        accepted => {
          val gameState = game.toState match {
            case g : GameRequested => g
            case _ =>  throw new IllegalStateException("State should have been subclass of [" + classOf[GameRequested].getName + "] but was " + game.toState)
          }
          if (accepted) { Games.update(gameState.accept(user.id)) }
          else { Games.update(gameState.reject(user.id)) }
          Redirect(routes.GamesController.game(game.id))
        })
    }
  }

  def question(gameId: GameId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => {
        if(game.isRequestor(user))
          GamesRequesteeController(gameId, questionId) match { // Use GamesRequesteeController here to get requestee quiz
            case Left(notFoundResult) => notFoundResult
            case Right((game, quiz, question)) => Ok(views.html.game.play.answeringQuestionRequestor(game.toState, quiz, question, None))
          }
        else if(game.isRequestee(user))
          GamesRequestorController(gameId, questionId) match { // Use GamesRequestorController here to get requestor quiz
            case Left(notFoundResult) => notFoundResult
            case Right((game, quiz, question)) => Ok(views.html.game.play.answeringQuestionRequestee(game.toState, quiz, question, None))
          }
        else throw new IllegalStateException("user was not requestee or requestor user = [" + user + "] game = [" + game + "]")
      }
    }
  }

  def answer(gameId: GameId, questionId: QuestionId, answerId: AnswerId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => {

        if(game.isRequestor(user))
          GamesRequesteeController(gameId, questionId) + AnswersController(questionId, answerId) match { // Use GamesRequesteeController here to get requestee quiz
            case Left(notFoundResult) => notFoundResult
            case Right((game, quiz, question, answer)) => Ok(views.html.game.play.answeringQuestionRequestor(game.toState, quiz, question, Some(Right(answer))))
          }

        else if(game.isRequestee(user))
          GamesRequestorController(gameId, questionId)  + AnswersController(questionId, answerId) match { // Use GamesRequestorController here to get requestor quiz
            case Left(notFoundResult) => notFoundResult
            case Right((game, quiz, question, answer)) => Ok(views.html.game.play.answeringQuestionRequestee(game.toState, quiz, question, Some(Right(answer))))
          }
        else throw new IllegalStateException("user was not requestee or requestor user = [" + user + "] game = [" + game + "]")
      }
    }
  }

  def reviewQuiz(gameId: GameId, quizId: QuizId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => {
        (game.gameRole(user), game.requesteeQuizIfDone(quizId), game.requestorQuizIfDone(quizId)) match {
          case (Unrelated, _, _) => throw new IllegalStateException("user was not requestee or requestor (user = [" + user + "] game = [" + game + "])")
          case (Requestor, Some(requesteeQuiz), None) => Ok(views.html.game.review.studentReview(game, requesteeQuiz, game.requestee))
          case (Requestor, None, Some(requestorQuiz)) => Ok(views.html.game.review.teacherReview(game, requestorQuiz, game.requestee))
          case (Requestee, Some(requesteeQuiz), None) => Ok(views.html.game.review.teacherReview(game, requesteeQuiz, game.requestor))
          case (Requestee, None, Some(requestorQuiz)) => Ok(views.html.game.review.studentReview(game, requestorQuiz, game.requestor))
          case (_, None, None) => throw new IllegalStateException("The game requested was not finished for user [" + user + "] quizId [" + quizId + "] game [" + game + "]")
          case error => throw new IllegalStateException("Should not be possible coding error (error[" + error + "] user [" + user + "] quizId [" + quizId + "] game [" + game + "])")
        }
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