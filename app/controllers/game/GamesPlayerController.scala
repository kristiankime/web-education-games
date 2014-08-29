package controllers.game

import com.artclod.mathml.Match._
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.util._
import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import controllers.game.GamesController._
import controllers.organization.CoursesController
import controllers.question.derivative.AnswersController._
import controllers.question.derivative.{AnswersController, QuestionsController, QuizzesController}
import controllers.support.SecureSocialConsented
import models.game._
import models.organization.{Course, Organization}
import models.question.derivative._
import models.support.{GameId, CourseId, OrganizationId}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Result, Controller}
import service.User
import play.api.db.slick.Config.driver.simple.Session
import models.support._

import scala.util.{Left, Right}

trait GamesPlayerController extends Controller with SecureSocialConsented {

  val playerType : String

  def createdQuiz(game: Game)(implicit session: Session) : Option[Quiz] // game.requesteeQuiz

  def createdQuizEnsured(game: Game)(implicit user: User, session: Session) : (Game, Quiz) // game.ensureRequesteeQuiz

  def quizToAnswer(game: Game)(implicit session: Session) : Option[Quiz] // game.requestorQuiz

  def finalizeQuiz(game: Game)(implicit session: Session) /*
          val gameState = game.toState match {
          case g : RequesteeQuiz => g
          case _ =>  throw new IllegalStateException("State should have been subclass of [" + classOf[RequesteeQuiz].getName + "] but was " + game.toState)
        }
        Games.update(gameState.finalizeRequesteeQuiz)
        */

  def finalizeAnswers(game: Game)(implicit session: Session) /*
          val gameState = game.toState match {
            case g : RequestorQuizFinished with RequesteeStillAnswering => g
            case _ =>  throw new IllegalStateException("State should have been subclass of RequestorQuizFinished with RequesteeStillAnswering but was " + game.toState)
          }
          Games.update(gameState.requesteeDoneAnswering) */

  def apply(gameId: GameId, questionId: QuestionId)(implicit session: Session): Either[Result, (Game, Quiz, Question)] =
    Games(gameId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no game for id=[" + gameId + "]")))
      case Some(game) => createdQuiz(game) match {
        case None => Left(NotFound(views.html.errors.notFoundPage("The game with id=[" + gameId + "] does not have a " + playerType + " Quiz")))
        case Some(quiz) => Right[Result, (Game, Quiz)]((game, quiz)) + QuestionsController(quiz.id, questionId)
      }
    }

  def create(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        GameCreateQuestion.form.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val (updatedGame, quiz) = createdQuizEnsured(game)
            val mathML = MathML(form._1).get // TODO better handle on error
            Questions.create(Question(null, user.id, mathML, form._2, JodaUTC.now), quiz.id)
            Redirect(routes.GamesController.game(game.id))
          })
    }
  }

  def remove(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        GameRemoveQuestion.form.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          questionId => {
            val (updatedGame, quiz) = createdQuizEnsured(game)
            for(question <- Questions(questionId)) { quiz.remove(question) }
            Redirect(routes.GamesController.game(game.id))
          })
    }
  }

  def quizDone(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => {

        finalizeQuiz(game)
        //        val gameState = game.toState match {
        //          case g : RequesteeQuiz => g
        //          case _ =>  throw new IllegalStateException("State should have been subclass of [" + classOf[RequesteeQuiz].getName + "] but was " + game.toState)
        //        }
        //        Games.update(gameState.finalizeRequesteeQuiz)

        Redirect(routes.GamesController.game(game.id))
      }
    }
  }

  def answer(gameId: GameId, questionId: QuestionId)= ConsentedAction { implicit request => implicit user => implicit session =>
   GamesRequestorController(gameId, questionId) match {
        case Left(notFoundResult) => notFoundResult
        case Right((game, quiz, question)) => {

          GameAnswerQuestion.values.bindFromRequest.fold(
            errors => BadRequest(views.html.errors.formErrorPage(errors)),
            form => {
              val math : MathMLElem = MathML(form._1).get // TODO better error handling
              val rawStr = form._2
              val unfinishedAnswer = UnfinishedAnswer(user.id, question.id, math, rawStr, JodaUTC.now)_
              Answers.correct(question, math) match {
                case Yes => Redirect(routes.GamesController.answer(game.id, question.id, Answers.createAnswer(unfinishedAnswer(true)).id))
                case No => Redirect(routes.GamesController.answer(game.id, question.id, Answers.createAnswer(unfinishedAnswer(false)).id))
                case Inconclusive => {
                  if(game.isRequestee(user)) Ok(views.html.game.answeringQuestionRequestee(game.toState, quiz, question, Some(Left(unfinishedAnswer(false)))))
                  else if(game.isRequestor(user)) Ok(views.html.game.answeringQuestionRequestor(game.toState, quiz, question, Some(Left(unfinishedAnswer(false)))))
                  else throw new IllegalStateException("User [" + user + "] wasn't Requestee or Requestor")
                }
              }
            })

        }
      }
    }

  def answeringDone(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => {

        finalizeAnswers(game)
        //        val gameState = game.toState match {
        //          case g : RequestorQuizFinished with RequesteeStillAnswering => g
        //          case _ =>  throw new IllegalStateException("State should have been subclass of RequestorQuizFinished with RequesteeStillAnswering but was " + game.toState)
        //        }
        //        Games.update(gameState.requesteeDoneAnswering)

        Redirect(routes.GamesController.game(game.id))
      }
    }
  }

}

object GameRemoveQuestion {
  val removeId = "removeId"
  val form = Form(removeId -> questionId)
}

object GameCreateQuestion {
  val mathML = "mathML"
  val rawStr = "rawStr"
  val form = Form(tuple(mathML -> text, rawStr -> text))
}

object GameAnswerQuestion {
  val mathML = "mathML"
  val rawStr = "rawStr"
  val values = Form(tuple(mathML -> text, rawStr -> text))
}