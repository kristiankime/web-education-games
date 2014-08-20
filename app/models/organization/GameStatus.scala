package models.organization

sealed trait GameStatus

object GameStatus {
  val respondRequest : GameStatus = RespondRequest
  val awaitingReponse : GameStatus = AwaitingReponse
  val gameRejected : GameStatus = GameRejected
  val createQuiz : GameStatus = CreateQuiz
  val awaitingQuiz : GameStatus = AwaitingQuiz
  val answerQuiz : GameStatus = AwaitingQuiz
  val awaitingAnswer : GameStatus = AwaitingQuiz
  val gameDone : GameStatus = AwaitingQuiz
}

object RespondRequest extends GameStatus

object AwaitingReponse extends GameStatus

object GameRejected extends GameStatus

object CreateQuiz extends GameStatus

object AwaitingQuiz extends GameStatus

object AnswerQuiz extends GameStatus

object AwaitingAnswer extends GameStatus

object GameDone extends GameStatus
