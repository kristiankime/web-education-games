package controllers.game

import models.game._
import models.question.derivative.Quiz
import play.api.db.slick.Config.driver.simple.Session
import service.User

object GamesRequestorController extends GamesPlayerController {

  val playerType: String = "Requestor"

  def createdQuiz(game: Game)(implicit session: Session): Option[Quiz] = game.requestorQuiz

  def createdQuizEnsured(game: Game)(implicit user: User, session: Session): (Game, Quiz) = game.ensureRequestorQuiz

  def quizToAnswer(game: Game)(implicit session: Session): Option[Quiz] = game.requesteeQuiz

  def finalizeQuiz(game: Game)(implicit session: Session) {
    val gameState = game.toState match {
      case g: RequestorQuiz => g
      case _ => throw new IllegalStateException("State should have been subclass of [" + classOf[RequestorQuiz].getName + "] but was " + game.toState)
    }
    Games.update(gameState.finalizeRequestorQuiz)
  }

  def finalizeAnswers(game: Game)(implicit session: Session) {
    val gameState = game.toState match {
      case g: RequesteeQuizFinished with RequestorStillAnswering => g
      case _ => throw new IllegalStateException("State should have been subclass of [RequesteeQuizFinished with RequestorStillAnswering] but was " + game.toState)
    }
    Games.update(gameState.requestorDoneAnswering)
  }

  //  def apply(gameId: GameId, questionId: QuestionId)(implicit session: Session): Either[Result, (Game, Quiz, Question)] =
  //    Games(gameId) match {
  //      case None => Left(NotFound(views.html.errors.notFoundPage("There was no game for id=[" + gameId + "]")))
  //      case Some(game) => game.requestorQuiz match {
  //        case None => Left(NotFound(views.html.errors.notFoundPage("The game with id=[" + gameId + "] is does not have a Requestee Quiz")))
  //        case Some(quiz) => Right[Result, (Game, Quiz)]((game, quiz)) + QuestionsController(quiz.id, questionId)
  //      }
  //    }
  //
  //  def create(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
  //    GamesController(gameId) match {
  //      case Left(notFoundResult) => notFoundResult
  //      case Right(game) =>
  //        GameRequestorCreate.form.bindFromRequest.fold(
  //          errors => BadRequest(views.html.errors.formErrorPage(errors)),
  //          form => {
  //            val (updatedGame, quiz) = game.ensureRequestorQuiz
  //            val mathML = MathML(form._1).get // TODO better handle on error
  //            Questions.create(Question(null, user.id, mathML, form._2, JodaUTC.now), quiz.id)
  //            Redirect(routes.GamesController.game(game.id))
  //          })
  //    }
  //  }
  //
  //  def remove(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
  //    GamesController(gameId) match {
  //      case Left(notFoundResult) => notFoundResult
  //      case Right(game) =>
  //        GameRequestorRemove.form.bindFromRequest.fold(
  //          errors => BadRequest(views.html.errors.formErrorPage(errors)),
  //          questionId => {
  //            val (updatedGame, quiz) = game.ensureRequestorQuiz
  //            for(question <- Questions(questionId)) { quiz.remove(question) }
  //            Redirect(routes.GamesController.game(game.id))
  //          })
  //    }
  //  }
  //
  //  def quizDone(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
  //    GamesController(gameId) match {
  //      case Left(notFoundResult) => notFoundResult
  //      case Right(game) => {
  //
  //        val gameState = game.toState match {
  //          case g : RequestorQuiz => g
  //          case _ =>  throw new IllegalStateException("State should have been subclass of [" + classOf[RequestorQuiz].getName + "] but was " + game.toState)
  //        }
  //        Games.update(gameState.finalizeRequestorQuiz)
  //
  //        Redirect(routes.GamesController.game(game.id))
  //      }
  //    }
  //  }
  //
  //  def answeringDone(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
  //    GamesController(gameId) match {
  //      case Left(notFoundResult) => notFoundResult
  //      case Right(game) => {
  //
  //        val gameState = game.toState match {
  //          case g : RequesteeQuizFinished with RequestorStillAnswering => g
  //          case _ =>  throw new IllegalStateException("State should have been subclass of RequesteeQuizFinished with RequestorStillAnswering but was " + game.toState)
  //        }
  //        Games.update(gameState.requestorDoneAnswering)
  //
  //        Redirect(routes.GamesController.game(game.id))
  //      }
  //    }
  //  }

}

//object GameRequestorRemove { // TODO rename to remove question
//  val removeId = "removeId"
//  val form = Form(removeId -> questionId)
//}
//
//object GameRequestorCreate {
//  val mathML = "mathML"
//  val rawStr = "rawStr"
//  val form = Form(tuple(mathML -> text, rawStr -> text))
//}
