package controllers.game

import models.game._
import models.quiz.Quiz
import models.quiz.answer.{TangentAnswer, Answer, DerivativeAnswer}
import models.quiz.question.{TangentQuestion, DerivativeQuestion, Question}
import models.support.{GameId, QuestionId}
import models.user.User
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc.Result

object GamesRequestorController extends GamesPlayerController {

  protected val playerType: String = "Requestor"

  protected def createdQuiz(game: Game)(implicit session: Session): Option[Quiz] = game.requestorQuiz

  protected def createdQuizEnsured(game: Game)(implicit user: User, session: Session): (Game, Quiz) = game.ensureRequestorQuiz

  protected def quizToAnswer(game: Game)(implicit session: Session): Option[Quiz] = game.requesteeQuiz

//  protected def finalizeQuizInternal(game: Game)(implicit session: Session) {
//    val gameState = game.toState match {
//      case g: RequestorQuiz => g
//      case _ => throw new IllegalStateException("State should have been subclass of [" + classOf[RequestorQuiz].getName + "] but was " + game.toState)
//    }
//    Games.update(gameState.finalizeRequestorQuiz)
//  }

//  protected def finalizeAnswersInternal(game: Game)(implicit session: Session) {
//    val gameState = game.toState match {
//      case g: RequesteeQuizFinished with RequestorStillAnswering => g
//      case _ => throw new IllegalStateException("State should have been subclass of [RequesteeQuizFinished with RequestorStillAnswering] but was " + game.toState)
//    }
//    Games.update(gameState.requestorDoneAnswering)
//  }

//  protected def questionView(game: Game, quiz: Quiz, question: Question, unfinishedAnswer: Answer)(implicit user: models.user.User, session: Session) : Result =
//    GamesController.questionViewRequestor(game.toState, quiz, question, Some(Left(unfinishedAnswer)))

  protected def questionToAnswer(gameId: GameId, questionId: QuestionId)(implicit session: Session): Either[Result, (Game, Quiz, Question)] =
    GamesRequesteeController(gameId, questionId)

}
