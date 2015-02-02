package controllers.game

import models.game._
import models.quiz.Quiz
import models.quiz.answer.DerivativeAnswer
import models.quiz.question.{Question, DerivativeQuestion}
import models.support.{QuestionId, GameId}
import models.user.User
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc.Result

object GamesRequesteeController extends GamesPlayerController {

  protected val playerType: String = "Requestee"

  protected def createdQuiz(game: Game)(implicit session: Session): Option[Quiz] = game.requesteeQuiz

  protected def createdQuizEnsured(game: Game)(implicit user: User, session: Session): (Game, Quiz) = game.ensureRequesteeQuiz

  protected def quizToAnswer(game: Game)(implicit session: Session): Option[Quiz] = game.requestorQuiz

  protected def finalizeQuizInternal(game: Game)(implicit session: Session) {
    val gameState = game.toState match {
      case g: RequesteeQuiz => g
      case _ => throw new IllegalStateException("State should have been subclass of [" + classOf[RequesteeQuiz].getName + "] but was " + game.toState)
    }
    Games.update(gameState.finalizeRequesteeQuiz)
  }

  protected def finalizeAnswersInternal(game: Game)(implicit session: Session) {
    val gameState = game.toState match {
      case g: RequestorQuizFinished with RequesteeStillAnswering => g
      case _ => throw new IllegalStateException("State should have been subclass of RequestorQuizFinished with RequesteeStillAnswering but was " + game.toState)
    }
    Games.update(gameState.requesteeDoneAnswering)
  }

  protected def answerViewInconclusive(game: Game, quiz: Quiz, question: DerivativeQuestion, unfinishedAnswer: (Boolean) => DerivativeAnswer )(implicit user: models.user.User, session: Session) : Result =
    Ok(views.html.game.play.answeringQuestionRequestor(game.toState, quiz, question, Some(Left(unfinishedAnswer(false)))))

  protected def questionToAnswer(gameId: GameId, questionId: QuestionId)(implicit session: Session): Either[Result, (Game, Quiz, Question)] =
    GamesRequestorController(gameId, questionId)

}