package controllers.game

import com.artclod.mathml.Match._
import com.artclod.mathml.MathML
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.question.derivative.QuestionsController
import controllers.support.SecureSocialConsented
import models.game._
import models.question.derivative._
import models.support._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc.{Controller, Result}
import service.User

import scala.util.{Left, Right}

trait GamesPlayerController extends Controller with SecureSocialConsented {

  // ===== Abstract "To Implement" =====
  protected val playerType: String

  protected def createdQuiz(game: Game)(implicit session: Session): Option[Quiz]

  protected def createdQuizEnsured(game: Game)(implicit user: User, session: Session): (Game, Quiz)

  protected def quizToAnswer(game: Game)(implicit session: Session): Option[Quiz]

  protected def finalizeQuizInternal(game: Game)(implicit session: Session)

  protected def finalizeAnswersInternal(game: Game)(implicit session: Session)

  protected def answerViewInconclusive(game: Game, quiz: Quiz, question: Question, unfinishedAnswer: (Boolean) => Answer )(implicit user: User,session: Session) : Result

  protected def questionToAnswer(gameId: GameId, questionId: QuestionId)(implicit session: Session): Either[Result, (Game, Quiz, Question)]

  // ===== Concrete =====
  def apply(gameId: GameId, questionId: QuestionId)(implicit session: Session): Either[Result, (Game, Quiz, Question)] =
    Games(gameId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no game for id=[" + gameId + "]")))
      case Some(game) => createdQuiz(game) match {
        case None => Left(NotFound(views.html.errors.notFoundPage("The game with id=[" + gameId + "] does not have a " + playerType + " Quiz")))
        case Some(quiz) => Right[Result, (Game, Quiz)]((game, quiz)) + QuestionsController(quiz.id, questionId)
      }
    }

  def createQuestion(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        GameCreateQuestion.form.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val (updatedGame, quiz) = createdQuizEnsured(game)
            val mathML = MathML(form._1).get // TODO better handle on error
            Questions.create(Question(null, user.id, mathML, form._2, JodaUTC.now), quiz.id)
            Redirect(routes.GamesController.game(updatedGame.id))
          })
    }
  }

  def removeQuestion(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        GameRemoveQuestion.form.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          questionId => {
            val (updatedGame, quiz) = createdQuizEnsured(game)
            for (question <- Questions(questionId)) { quiz.remove(question) }
            Redirect(routes.GamesController.game(updatedGame.id))
          })
    }
  }

  def finalizeCreatedQuiz(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => {
        finalizeQuizInternal(game)
        Redirect(routes.GamesController.game(game.id))
      }
    }
  }

  def answerQuestion(gameId: GameId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    questionToAnswer(gameId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((game, quiz, question)) => {

        GameAnswerQuestion.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val math: MathMLElem = MathML(form._1).get // TODO better error handling
            val rawStr = form._2
            val unfinishedAnswer = UnfinishedAnswer(user.id, question.id, math, rawStr, JodaUTC.now) _
            Answers.correct(question, math) match {
              case Yes => Redirect(routes.GamesController.answer(game.id, question.id, Answers.createAnswer(unfinishedAnswer(true)).id))
              case No => Redirect(routes.GamesController.answer(game.id, question.id, Answers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => answerViewInconclusive(game, quiz, question, unfinishedAnswer)
            }
          })

      }
    }
  }

  def finalizeAnswers(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => {
        finalizeAnswersInternal(game)
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