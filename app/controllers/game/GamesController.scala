package controllers.game

import com.artclod.play.CommonsMailerHelper
import com.artclod.util._
import controllers.game.GamesEmail._
import controllers.organization.CoursesController
import controllers.quiz.AnswersController
import controllers.quiz.tangent.TangentQuestionForm
import controllers.support.SecureSocialConsented
import models.game.GameRole._
import models.game._
import models.organization._
import models.quiz.Quiz
import models.quiz.answer.{TangentAnswer, DerivativeAnswer, Answer, DerivativeAnswers}
import models.quiz.question.{TangentQuestion, DerivativeQuestion, Question}
import models.support._
import models.user.{User, Users}
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc._

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

  def requestGame(organizationId: OrganizationId, courseId: CourseId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    CoursesController(organizationId, courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course)) => GameRequest.form.bindFromRequest.fold(
        errors => BadRequest(views.html.errors.formErrorPage(errors)),
        otherUserId => Games.activeGame(user.id, otherUserId) match {
          case Some(game) => Redirect(controllers.game.routes.GamesController.game(game.id, None)) // TODO accept game if in awaiting state
          case None => {

            val otherUser = Users(otherUserId).get // TODO

            val game = Games.request(user, otherUser, course)
            for(mail <- otherUser.maybeSendGameEmail.map(otherMail => CommonsMailerHelper.defaultMailSetup(otherMail))) {
              val userName = user.nameDisplay
              mail.setSubject("CalcTutor game request from " + userName)
              mail.sendHtml(userName + " has requested to play a game with you in the " + serverLinkEmail(request) + " (" + goToGameLinkEmail(request, game) + ").")
            }
            Redirect(controllers.game.routes.GamesController.game(game.id, None))
          }
        })
    }
  }

  def game(gameId: GameId, answerIdOp: Option[AnswerId]) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        if(game.isRequestor(user)) game.toState match {
          case state: GameRejected => Ok(views.html.game.request.rejectedRequestor(state))
          case state: RequestorDoneAnswering => Ok(views.html.game.play.requestor.gameDoneRequestor(state))
          case state: RequestorQuiz => Ok(views.html.game.play.requestor.createQuizRequestor(state, TangentQuestionForm.values))
          case state: RequestorQuizFinished with RequesteeQuiz => Ok(views.html.game.play.requestor.awaitingQuizRequestor(state))
          case state: RequestorQuizFinished with RequesteeQuizFinished => Ok(views.html.game.play.requestor.answeringQuizRequestor(state, answerIdOp.flatMap(id => DerivativeAnswers(id))))
          case _ =>  throw new IllegalStateException("No match in Requestor State, programming error")
        }
        else if(game.isRequestee(user)) game.toState match {
          case state: GameRejected => Ok(views.html.game.request.rejectedRequestee(state))
          case state: RequesteeDoneAnswering => Ok(views.html.game.play.requestee.gameDoneRequestee(state))
          case state: GameRequested => Ok(views.html.game.request.responedToGameRequest(state))
          case state: RequesteeQuiz => Ok(views.html.game.play.requestee.createQuizRequestee(state, TangentQuestionForm.values))
          case state: RequesteeQuizFinished with RequestorQuiz => Ok(views.html.game.play.requestee.awaitingQuizRequestee(state))
          case state: RequestorQuizFinished with RequesteeQuizFinished => Ok(views.html.game.play.requestee.answeringQuizRequestee(state, answerIdOp.flatMap(id => DerivativeAnswers(id))))
          case _ =>  throw new IllegalStateException("No match in Requestee State, programming error")
        }
        else throw new IllegalStateException("TODO code up teacher view")
      }
    }

  def respond(gameId: GameId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => GameResponse.form.bindFromRequest.fold(
        errors => BadRequest(views.html.errors.formErrorPage(errors)),
        accepted => {
          val gameState = game.toState match {
            case g : GameRequested => g
            case _ =>  throw new IllegalStateException("State should have been subclass of [" + classOf[GameRequested].getName + "] but was " + game.toState)
          }
          if (accepted) {
            Games.update(gameState.accept(user.id))
            for(mail <- gameState.game.otherPlayer(user).maybeSendGameEmail.map(otherMail => CommonsMailerHelper.defaultMailSetup(otherMail))) {
              val userName = user.nameDisplay
              mail.setSubject(userName + " accepted your CalcTutor game request")
              mail.sendHtml(userName + " accepted your requests to play a game with you in the " + serverLinkEmail(request) + " (" + goToGameLinkEmail(request, game) + ").")
            }
          }
          else {
            Games.update(gameState.reject(user.id))
            for(mail <- gameState.game.otherPlayer(user).maybeSendGameEmail.map(otherMail => CommonsMailerHelper.defaultMailSetup(otherMail))) {
              val userName = user.nameDisplay
              mail.setSubject(userName + " rejected your CalcTutor game request")
              mail.sendHtml(userName + " rejected your requests to play a game with you in the " + serverLinkEmail(request))
            }
          }
          Redirect(routes.GamesController.game(game.id, None))
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
            case Right((game, quiz, question)) => questionViewRequestor(game.toState, quiz, question, None)
          }
        else if(game.isRequestee(user))
          GamesRequestorController(gameId, questionId) match { // Use GamesRequestorController here to get requestor quiz
            case Left(notFoundResult) => notFoundResult
            case Right((game, quiz, question)) => questionViewRequestee(game.toState, quiz, question, None)
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
            case Right((game, quiz, question, answer)) =>  questionViewRequestor(game.toState, quiz, question, Some(Right(answer)))
          }
        else if(game.isRequestee(user))
          GamesRequestorController(gameId, questionId)  + AnswersController(questionId, answerId) match { // Use GamesRequestorController here to get requestor quiz
            case Left(notFoundResult) => notFoundResult
            case Right((game, quiz, question, answer)) => questionViewRequestee(game.toState, quiz, question, Some(Right(answer)))
          }
        else throw new IllegalStateException("user was not requestee or requestor user = [" + user + "] game = [" + game + "]")
      }
    }
  }

  // LATER figure out how to ensure Option[Either[DerivativeAnswer,DerivativeAnswer]] etc
  def questionViewRequestor(gameState: GameState, quiz: Quiz, question: Question, answer: Option[Either[Answer, Answer]])(implicit user: User, session: Session) : Result =
    (question, answer) match {
      case (q : DerivativeQuestion, a : Option[Either[DerivativeAnswer,DerivativeAnswer]]) => Ok(views.html.game.play.requestor.answeringDerivativeQuestionRequestor(gameState, quiz, q, a))
      case (q : TangentQuestion, a : Option[Either[TangentAnswer,TangentAnswer]]) => Ok(views.html.game.play.requestor.answeringTangentQuestionRequestor(gameState, quiz, q, a))
    }

  // LATER figure out how to ensure Option[Either[DerivativeAnswer,DerivativeAnswer]] etc
  def questionViewRequestee(gameState: GameState, quiz: Quiz, question: Question, answer: Option[Either[Answer, Answer]])(implicit user: User, session: Session) : Result =
    (question, answer) match {
      case (q : DerivativeQuestion, a : Option[Either[DerivativeAnswer,DerivativeAnswer]]) => Ok(views.html.game.play.requestee.answeringDerivativeQuestionRequestee(gameState, quiz, q, a))
      case (q : TangentQuestion, a : Option[Either[TangentAnswer,TangentAnswer]]) => Ok(views.html.game.play.requestee.answeringTangentQuestionRequestee(gameState, quiz, q, a))
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

object  GameRequest {
  val requestee = "requestee"
  val form = Form(requestee -> userId)
}

object GameResponse {
  val response = "reponse"
  val form = Form(response -> boolean)
}