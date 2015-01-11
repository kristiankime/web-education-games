package controllers.game

import com.artclod.mathml.Match._
import com.artclod.mathml.MathML
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.play.CommonsMailerHelper
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.game.GamesEmail._
import controllers.quiz.QuestionsController
import controllers.quiz.derivative.DerivativeQuestionForm
import controllers.support.SecureSocialConsented
import models.game._
import models.quiz.Quiz
import models.quiz.answer.{DerivativeAnswers, DerivativeAnswerUnfinished, DerivativeAnswer}
import models.quiz.question.{DerivativeQuestions, DerivativeQuestion}
import models.support._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc.{Controller, Result}
import service.User
import models.user.UserPimped
import scala.util.{Left, Right}

trait GamesPlayerController extends Controller with SecureSocialConsented {

  // ===== Abstract "To Implement" =====
  protected val playerType: String

  protected def createdQuiz(game: Game)(implicit session: Session): Option[Quiz]

  protected def createdQuizEnsured(game: Game)(implicit user: User, session: Session): (Game, Quiz)

  protected def quizToAnswer(game: Game)(implicit session: Session): Option[Quiz]

  protected def finalizeQuizInternal(game: Game)(implicit session: Session)

  protected def finalizeAnswersInternal(game: Game)(implicit session: Session)

  protected def answerViewInconclusive(game: Game, quiz: Quiz, question: DerivativeQuestion, unfinishedAnswer: (Boolean) => DerivativeAnswer )(implicit user: User,session: Session) : Result

  protected def questionToAnswer(gameId: GameId, questionId: QuestionId)(implicit session: Session): Either[Result, (Game, Quiz, DerivativeQuestion)]

  // ===== Concrete =====
  def apply(gameId: GameId, questionId: QuestionId)(implicit session: Session): Either[Result, (Game, Quiz, DerivativeQuestion)] =
    Games(gameId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no game for id=[" + gameId + "]")))
      case Some(game) => createdQuiz(game) match {
        case None => Left(NotFound(views.html.errors.notFoundPage("The game with id=[" + gameId + "] does not have a " + playerType + " Quiz")))
        case Some(quiz) => Right[Result, (Game, Quiz)]((game, quiz)) + QuestionsController(quiz.id, questionId)
      }
    }

  def createQuestion(gameId: GameId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        DerivativeQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val (updatedGame, quiz) = createdQuizEnsured(game)
            val mathML = MathML(form._1).get // TODO better handle on error
            DerivativeQuestions.create(DerivativeQuestion(null, user.id, mathML, form._2, JodaUTC.now), quiz.id)
            Redirect(routes.GamesController.game(updatedGame.id, None))
          })
    }
  }

  def removeQuestion(gameId: GameId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        GameRemoveQuestion.form.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          questionId => {
            val (updatedGame, quiz) = createdQuizEnsured(game)
            for (question <- DerivativeQuestions(questionId)) { quiz.remove(question) }
            Redirect(routes.GamesController.game(updatedGame.id, None))
          })
    }
  }

  def finalizeCreatedQuiz(gameId: GameId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => {
        finalizeQuizInternal(game)
        for(mail <- game.otherPlayer(user).maybeSendGameEmail.map(otherMail => CommonsMailerHelper.defaultMailSetup(otherMail))) {
          val userName = user.name
          mail.setSubject(userName + " created a CalcTutor game quiz for you")
          mail.sendHtml(userName + " created a game quiz for you in the " + serverLinkEmail(request) + " (" + goToGameLinkEmail(request, game) + ").")
        }
        Redirect(routes.GamesController.game(game.id, None))
      }
    }
  }

  def answerQuestion(gameId: GameId, questionId: QuestionId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    questionToAnswer(gameId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((game, quiz, question)) => {

        GameAnswerQuestion.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val math: MathMLElem = MathML(form._1).get // TODO better error handling
            val rawStr = form._2
            val unfinishedAnswer = DerivativeAnswerUnfinished(user.id, question.id, math, rawStr, JodaUTC.now) _
            DerivativeAnswers.correct(question, math) match {
//              case Yes => Redirect(routes.GamesController.answer(game.id, question.id, Answers.createAnswer(unfinishedAnswer(true)).id))
              case Yes => Redirect(routes.GamesController.game(game.id, Some(DerivativeAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case No => Redirect(routes.GamesController.answer(game.id, question.id, DerivativeAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => answerViewInconclusive(game, quiz, question, unfinishedAnswer)
            }
          })

      }
    }
  }

  def finalizeAnswers(gameId: GameId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => {
        finalizeAnswersInternal(game)
        for(mail <- game.otherPlayer(user).maybeSendGameEmail.map(otherMail => CommonsMailerHelper.defaultMailSetup(otherMail))) {
          val userName = user.name
          mail.setSubject(userName + " finished answering your CalcTutor game quiz")
          mail.sendHtml(userName + " finished answering your game quiz in the " + serverLinkEmail(request) + " (" + goToGameLinkEmail(request, game) + ").")
        }
        Redirect(routes.GamesController.game(game.id, None))
      }
    }
  }

}

object GameRemoveQuestion {
  val removeId = "removeId"
  val form = Form(removeId -> questionId)
}

object GameAnswerQuestion {
  val mathML = "mathML"
  val rawStr = "rawStr"
  val values = Form(tuple(mathML -> text, rawStr -> text))
}