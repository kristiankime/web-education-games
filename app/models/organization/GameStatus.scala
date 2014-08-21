package models.organization

sealed trait GameStatus

object GameStatus {
  val respondRequest : GameStatus = new GameStatus(){} // RespondRequestStatus
  val awaitingReponse : GameStatus = new GameStatus(){} // AwaitingReponseStatus
  val gameRejected : GameStatus = new GameStatus(){} // GameRejectedStatus
  val createQuiz : GameStatus = new GameStatus(){} // CreateQuizStatus
  val awaitingQuiz : GameStatus = new GameStatus(){} // AwaitingQuizStatus
  val answerQuiz : GameStatus = new GameStatus(){} // AwaitingQuizStatus
  val awaitingAnswer : GameStatus = new GameStatus(){} // AwaitingQuizStatus
  val gameDone : GameStatus = new GameStatus(){} // AwaitingQuizStatus
}

object RespondRequestStatus extends GameStatus

object AwaitingReponseStatus extends GameStatus

object GameRejectedStatus extends GameStatus

object CreateQuizStatus extends GameStatus

object AwaitingQuizStatus extends GameStatus

object AnswerQuizStatus extends GameStatus

object AwaitingAnswerStatus extends GameStatus

object GameDoneStatus extends GameStatus
