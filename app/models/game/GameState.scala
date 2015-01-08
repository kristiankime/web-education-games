package models.game

import models.quiz.Quizzes
import models.support.QuizId
import play.api.db.slick.Config.driver.simple._
import models.user.Users

object GameState {
  val numberOfQuestions = 3
}

sealed trait GameState {
    self: GameResponse with RequestorQuizStatus with RequesteeQuizStatus with RequestorAnswerStatus with RequesteeAnswerStatus =>

   val game : Game

   def checks: Unit = {
     responseCheck
     requestorQuizStatusCheck
     requesteeQuizStatusCheck
     requestorAnswerStatusCheck
     requesteeAnswerStatusCheck
   }
}

// Game in requested state (requestor can still create a quiz while waiting)
case class RequestedNoQuiz(val game: Game) extends GameState with GameRequested with RequestorQuiz with RequesteeQuiz with BothStillAnswering { checks }
case class RequestedQuizDone(val game: Game) extends GameState with GameRequested with RequestorQuizFinished with RequesteeQuiz with BothStillAnswering { checks }
// Game was rejected (but requestor could have made a quiz before it was rejected)
case class RejectedNoQuiz(val game: Game) extends GameState with GameRejected with RequestorQuiz with RequesteeQuiz with BothStillAnswering { checks }
case class RejectedQuizDone(val game: Game) extends GameState with GameRejected with RequestorQuizFinished with RequesteeQuiz with BothStillAnswering { checks }
// Game is in progress both players are making quizzes
case class AcceptedTorNoQuizTeeNoQuiz(val game: Game) extends GameState with GameAccepted with RequestorQuiz with RequesteeQuiz with BothStillAnswering { checks }
case class AcceptedTorQuizDoneTeeNoQuiz(val game: Game) extends GameState with GameAccepted with  RequestorQuizFinished with RequesteeQuiz with BothStillAnswering { checks }
case class AcceptedNoTorQuizTeeQuizDone(val game: Game) extends GameState with GameAccepted with RequestorQuiz with RequesteeQuizFinished with BothStillAnswering { checks }
// Game is in progress both quizzes have been made and players are answering
case class QuizzesDoneTorAnsTeeAns(val game: Game) extends GameState with GameAccepted with BothQuizzesDone with BothStillAnswering { checks }
case class QuizzesDoneTorAnsTeeDone(val game: Game) extends GameState with GameAccepted with BothQuizzesDone with RequestorStillAnswering with RequesteeDoneAnswering { checks }
case class QuizzesDoneTorDoneTeeAnd(val game: Game) extends GameState with GameAccepted with  BothQuizzesDone with RequestorDoneAnswering with RequesteeStillAnswering { checks }
case class GameDone(val game: Game) extends GameState with GameAccepted with BothQuizzesDone with BothDoneAnswering { checks }
