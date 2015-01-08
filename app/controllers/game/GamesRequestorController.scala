package controllers.game

import models.game._
import models.quiz.Quiz
import models.quiz.answer.DerivativeAnswer
import models.quiz.question.DerivativeQuestion
import models.support.{QuestionId, GameId}
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc.Result
import service.User

object GamesRequestorController extends GamesPlayerController {

  protected val playerType: String = "Requestor"

  protected def createdQuiz(game: Game)(implicit session: Session): Option[Quiz] = game.requestorQuiz

  protected def createdQuizEnsured(game: Game)(implicit user: User, session: Session): (Game, Quiz) = game.ensureRequestorQuiz

  protected def quizToAnswer(game: Game)(implicit session: Session): Option[Quiz] = game.requesteeQuiz

  protected def finalizeQuizInternal(game: Game)(implicit session: Session) {
    val gameState = game.toState match {
      case g: RequestorQuiz => g
      case _ => throw new IllegalStateException("State should have been subclass of [" + classOf[RequestorQuiz].getName + "] but was " + game.toState)
    }
    Games.update(gameState.finalizeRequestorQuiz)
  }

  protected def finalizeAnswersInternal(game: Game)(implicit session: Session) {
    val gameState = game.toState match {
      case g: RequesteeQuizFinished with RequestorStillAnswering => g
      case _ => throw new IllegalStateException("State should have been subclass of [RequesteeQuizFinished with RequestorStillAnswering] but was " + game.toState)
    }
    Games.update(gameState.requestorDoneAnswering)
  }

  protected def answerViewInconclusive(game: Game, quiz: Quiz, question: DerivativeQuestion, unfinishedAnswer: (Boolean) => DerivativeAnswer )(implicit user: User,session: Session) : Result =
    Ok(views.html.game.play.answeringQuestionRequestee(game.toState, quiz, question, Some(Left(unfinishedAnswer(false)))))

  protected def questionToAnswer(gameId: GameId, questionId: QuestionId)(implicit session: Session): Either[Result, (Game, Quiz, DerivativeQuestion)] =
    GamesRequesteeController(gameId, questionId)

}
