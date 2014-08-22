package models.game

import play.api.db.slick.Config.driver.simple._
import models.organization.{GameResponseStatus, Game}
import models.user.Users

sealed trait GameState {
    self: GameResponse
     with RequestorQuizCreateStatus with RequestorQuizDoneStatus
     with RequesteeQuizCreateStatus with RequesteeQuizDoneStatus =>

   val game : Game

   def checks: Unit = {
     responseCheck
     requestorQuizCreateStatusCheck
     requestorQuizDoneStatusCheck
     requesteeQuizCreateStatusCheck
     requesteeQuizDoneStatusCheck
   }
}

case class RequestedNoQuiz(val game: Game) extends GameState with GameRequested with RequestorNoQuiz with RequestorQuizUnfinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
case class RequestedQuiz(val game: Game) extends GameState with  GameRequested with RequestorQuiz with RequestorQuizUnfinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
case class RequestedQuizDone(val game: Game) extends GameState with GameRequested with RequestorQuiz with RequestorQuizFinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }

case class RejectedNoQuiz(val game: Game) extends GameState with GameRejected with RequestorNoQuiz with RequestorQuizUnfinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
case class RejectedQuiz(val game: Game) extends GameState with GameRejected with RequestorQuiz with RequestorQuizUnfinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
case class RejectedQuizDone(val game: Game) extends GameState with GameRejected with RequestorQuiz with RequestorQuizFinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }

case class AcceptedTorNoQuizTeeNoQuiz(val game: Game) extends GameState with GameAccepted with RequestorNoQuiz with RequestorQuizUnfinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
case class AcceptedTorQuizTeeNoQuiz(val game: Game) extends GameState with GameAccepted with RequestorQuiz with RequestorQuizUnfinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
case class AcceptedTorQuizDoneTeeNoQuiz(val game: Game) extends GameState with GameAccepted with RequestorQuiz with RequestorQuizFinished with RequesteeNoQuiz with RequesteeQuizUnfinished { checks }
case class AcceptedTorNoQuizTeeQuiz(val game: Game) extends GameState with GameAccepted with RequestorNoQuiz with RequestorQuizUnfinished with RequesteeQuiz with RequesteeQuizUnfinished { checks }
case class AcceptedTorQuizTeeQuiz(val game: Game) extends GameState with GameAccepted with RequestorQuiz with RequestorQuizUnfinished with RequesteeQuiz with RequesteeQuizUnfinished { checks }
case class AcceptedTorQuizDoneTeeQuiz(val game: Game) extends GameState with GameAccepted with RequestorQuiz with RequestorQuizFinished with RequesteeQuiz with RequesteeQuizUnfinished { checks }
case class AcceptedTorNoQuizTeeQuizDone(val game: Game) extends GameState with GameAccepted with RequestorNoQuiz with RequestorQuizUnfinished with RequesteeQuiz with RequesteeQuizFinished { checks }
case class AcceptedTorQuizTeeQuizDone(val game: Game) extends GameState with GameAccepted with RequestorQuiz with RequestorQuizUnfinished with RequesteeQuiz with RequesteeQuizFinished{ checks }
case class AcceptedTorQuizDoneTeeQuizDone(val game: Game) extends GameState with GameAccepted with RequestorQuiz with RequestorQuizFinished with RequesteeQuiz with RequesteeQuizFinished { checks }
