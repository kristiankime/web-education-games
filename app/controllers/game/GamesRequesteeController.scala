package controllers.game

import models.game._
import models.quiz.Quiz
import models.quiz.answer.{TangentAnswer, Answer, DerivativeAnswer}
import models.quiz.question.{TangentQuestion, DerivativeQuestion, Question}
import models.support.{GameId, QuestionId}
import models.user.User
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc.Result

object GamesRequesteeController extends GamesPlayerController {

  protected val playerType: String = "Requestee"

  protected def createdQuiz(game: Game)(implicit session: Session): Option[Quiz] = game.requesteeQuiz

  protected def createdQuizEnsured(game: Game)(implicit user: User, session: Session): (Game, Quiz) = game.ensureRequesteeQuiz

  protected def quizToAnswer(game: Game)(implicit session: Session): Option[Quiz] = game.requestorQuiz

  protected def questionToAnswer(gameId: GameId, questionId: QuestionId)(implicit session: Session): Either[Result, (Game, Quiz, Question)] =
    GamesRequestorController(gameId, questionId)

}