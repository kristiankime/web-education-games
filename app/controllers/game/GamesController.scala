package controllers.game

import com.artclod.play.CommonsMailerHelper
import com.artclod.util._
import controllers.game.GamesEmail._
import controllers.organization.CoursesController
import controllers.quiz.AnswersController
import controllers.quiz.derivative.DerivativeQuestionForm
import controllers.quiz.derivativegraph.DerivativeGraphQuestionForm
import controllers.quiz.tangent.TangentQuestionForm
import controllers.support.{RequireAccess, SecureSocialConsented}
import models.game.GameRole._
import models.game._
import models.game.mask.{NeedToRespond, GameMask}
import models.organization._
import models.quiz.Quiz
import models.quiz.answer._
import models.quiz.question._
import models.support._
import models.user.{User, Users}
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc._
import service.Edit

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
          case Some(game) => Redirect(controllers.game.routes.GamesController.game(game.id, None)) // TODO accept game if in awaiting state
          case None => {

            val otherUser = Users(otherUserId).get // TODO

            val game = Games.request(user, otherUser, course)
            for(mail <- otherUser.maybeSendEmail.map(otherMail => CommonsMailerHelper.defaultMailSetup(otherMail))) {
              val userName = user.nameDisplay
              mail.setSubject("CalcTutor game request from " + userName)
              mail.sendHtml(userName + " has requested to play a game with you in the " + serverLinkEmail(request) + " (" + goToGameLinkEmail(request, game) + ").")
            }
            Redirect(controllers.game.routes.GamesController.game(game.id, None))
          }
        })
    }
  }

  def summary(gameId: GameId) = ConsentedAction(RequireAccess(Edit, gameId)) { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => Ok(views.html.game.review.summary(game))
    }
  }

  def game(gameId: GameId, answerIdOp: Option[AnswerId]) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => {
        game.toMask(user) match  {
          case mask : mask.ResponseRequired              => Ok(views.html.game.request.responding(mask))
          case mask : mask.ResponseRequiredOtherQuiz     => Ok(views.html.game.request.responding(mask))
          case mask : mask.RejectedNoQuiz                => Ok(views.html.game.request.rejected(mask))
          case mask : mask.RejectedMeQuizDone            => Ok(views.html.game.request.rejected(mask))
          case mask : mask.RejectedOtherQuizDone         => Ok(views.html.game.request.rejected(mask))
          case mask : mask.RequestedNoQuiz               => Ok(views.html.game.play.createQuiz(mask, controllers.quiz.QuestionForms.empty))
          case mask : mask.AcceptedMeNoQuizOtherNoQuiz   => Ok(views.html.game.play.createQuiz(mask, controllers.quiz.QuestionForms.empty))
          case mask : mask.AcceptedMeNoQuizOtherQuizDone => Ok(views.html.game.play.createQuiz(mask, controllers.quiz.QuestionForms.empty))
          case mask : mask.RequestedQuizDone             => Ok(views.html.game.play.awaitingQuiz(mask))
          case mask : mask.AcceptedMeQuizDoneOtherNoQuiz => Ok(views.html.game.play.awaitingQuiz(mask))
          case mask : mask.QuizzesDoneMeAnsOtherAns      => Ok(views.html.game.play.answeringQuiz(mask, answerIdOp.flatMap(id => Answers(id)) ))
          case mask : mask.QuizzesDoneMeAnsOtherDone     => Ok(views.html.game.play.answeringQuiz(mask, answerIdOp.flatMap(id => Answers(id)) ))
          case mask : mask.QuizzesDoneMeDoneOtherAns     => Ok(views.html.game.play.gameDone(mask))
          case mask : mask.GameDone                      => Ok(views.html.game.play.gameDone(mask))
        }
      }
    }
  }

  def addQuestion(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => {
        game.toMask(user) match {
          case mask : mask.MyQuizUnfinished => {
            GameAddQuestion.form.bindFromRequest.fold(
              errors => BadRequest(views.html.errors.formErrorPage(errors)),
              questionIdNum => Ok(views.html.game.play.createQuiz(mask, controllers.quiz.QuestionForms.empty))
            )
          }
          case _ =>  BadRequest(views.html.errors.errorPage(new IllegalStateException("Can only add to game quizzes when they are unfinished state ["  + gameId + "]")))
        }
      }
    }
  }

  def respond(gameId: GameId) = ConsentedAction{ implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => GameResponse.form.bindFromRequest.fold(
        errors => BadRequest(views.html.errors.formErrorPage(errors)),
        accepted => {
          val gameState = game.toMask(user) match {
            case g : mask.NeedToRespond => g
            case s  =>  throw new IllegalStateException("State should have been subclass of " + classOf[NeedToRespond].getName + " but was " + s)
          }
          if (accepted) {
            Games.update(gameState.accept)
            for(mail <- gameState.game.otherPlayer(user).maybeSendEmail.map(otherMail => CommonsMailerHelper.defaultMailSetup(otherMail))) {
              val userName = user.nameDisplay
              mail.setSubject(userName + " accepted your CalcTutor game request")
              mail.sendHtml(userName + " accepted your requests to play a game with you in the " + serverLinkEmail(request) + " (" + goToGameLinkEmail(request, game) + ").")
            }
          }
          else {
            Games.update(gameState.reject)
            for(mail <- gameState.game.otherPlayer(user).maybeSendEmail.map(otherMail => CommonsMailerHelper.defaultMailSetup(otherMail))) {
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
            case Right((game, quiz, question)) => questionView(game.toMask(user), quiz, question, None)
          }
        else if(game.isRequestee(user))
          GamesRequestorController(gameId, questionId) match { // Use GamesRequestorController here to get requestor quiz
            case Left(notFoundResult) => notFoundResult
            case Right((game, quiz, question)) => questionView(game.toMask(user), quiz, question, None)
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
            case Right((game, quiz, question, answer)) =>  questionView(game.toMask(user), quiz, question, Some(Right(answer)))
          }
        else if(game.isRequestee(user))
          GamesRequestorController(gameId, questionId)  + AnswersController(questionId, answerId) match { // Use GamesRequestorController here to get requestor quiz
            case Left(notFoundResult) => notFoundResult
            case Right((game, quiz, question, answer)) => questionView(game.toMask(user), quiz, question, Some(Right(answer)))
          }
        else throw new IllegalStateException("user was not requestee or requestor user = [" + user + "] game = [" + game + "]")
      }
    }
  }

  // LATER figure out how to ensure Option[Either[DerivativeAnswer,DerivativeAnswer]] etc
  def questionView(gameState: GameMask, quiz: Quiz, question: Question, answer: Option[Either[Answer, Answer]])(implicit user: User, session: Session) : Result =
    (question, answer) match {
      case (q : DerivativeQuestion, a : Option[Either[DerivativeAnswer,DerivativeAnswer]])                   => Ok(views.html.game.question.answeringDerivativeQuestion(gameState, quiz, q, a))
      case (q : DerivativeGraphQuestion, a : Option[Either[DerivativeGraphAnswer,DerivativeGraphAnswer]])    => Ok(views.html.game.question.answeringDerivativeGraphQuestion(gameState, quiz, q, a))
      case (q : TangentQuestion, a : Option[Either[TangentAnswer,TangentAnswer]])                            => Ok(views.html.game.question.answeringTangentQuestion(gameState, quiz, q, a))
      case (q : GraphMatchQuestion, a : Option[Either[GraphMatchAnswer,GraphMatchAnswer]])                   => Ok(views.html.game.question.answeringGraphMatchQuestion(gameState, quiz, q, a))
      case (q : PolynomialZoneQuestion, a : Option[Either[PolynomialZoneAnswer,PolynomialZoneAnswer]])       => Ok(views.html.game.question.answeringPolynomialZoneQuestion(gameState, quiz, q, a))
      case (q : MultipleChoiceQuestion, a : Option[Either[MultipleChoiceAnswer,MultipleChoiceAnswer]])       => Ok(views.html.game.question.answeringMultipleChoiceQuestion(gameState, quiz, q, a))
      case (q : MultipleFunctionQuestion, a : Option[Either[MultipleFunctionAnswer,MultipleFunctionAnswer]]) => Ok(views.html.game.question.answeringMultipleFunctionQuestion(gameState, quiz, q, a))
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

  def list(organizationId: OrganizationId, courseId: CourseId) = ConsentedAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    CoursesController(organizationId, courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course)) => Ok(views.html.organization.courseGames(organization, course))
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

object GameAddQuestion {
  val question = "question"
  val form = Form(question -> number)
}