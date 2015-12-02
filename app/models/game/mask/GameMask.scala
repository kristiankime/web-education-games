package models.game.mask

import models.game.Game
import models.support.{QuizId, UserId}
import models.user.{Users, User}
import play.api.db.slick.Config.driver.simple.Session

object GameMask {
  val numberOfQuestions = 3
}

trait GameSetup {
  val game : Game
  val meId : UserId
  def me(implicit session: Session) = Users(meId).get
  val otherId : UserId
  def other(implicit session: Session) = Users(otherId).get


  val requestee : Boolean = game.requesteeId == meId
  val requestor : Boolean = game.requestorId == meId

  def mySkill =                           if(requestee) { game.requesteeSkill   } else { game.requestorSkill     }
  def myQuizId      : Option[QuizId] =    if(requestee) { game.requesteeQuizId   } else { game.requestorQuizId   }
  def myQuizDone    : Boolean =           if(requestee) { game.requesteeQuizDone } else { game.requestorQuizDone }
  def myQuiz(implicit session: Session) = if(requestee) { game.requesteeQuiz.get } else { game.requestorQuiz.get }
  def myFinished: Boolean =               if(requestee) { game.requesteeFinished } else { game.requestorFinished }

  def otherSkill =                           if(requestee) { game.requestorSkill   } else { game.requesteeSkill     }
  def otherQuizId   : Option[QuizId] =       if(requestee) { game.requestorQuizId   } else { game.requesteeQuizId   }
  def otherQuizDone : Boolean =              if(requestee) { game.requestorQuizDone } else { game.requesteeQuizDone }
  def otherQuiz(implicit session: Session) = if(requestee) { game.requestorQuiz.get } else { game.requesteeQuiz.get }
  def otherFinished: Boolean =               if(requestee) { game.requestorFinished } else { game.requesteeFinished }
}

sealed trait GameMask extends GameSetup {
   self: ResponseStatus with MyQuizStatus with OtherQuizStatus with MyAnswerStatus with OtherAnswerStatus =>

   def checks: Unit = {
     if(meId    != game.requesteeId &&  meId != game.requestorId   ) { throw new IllegalStateException("me wasn't requestee or requestorId")    }
     if(otherId != game.requesteeId &&  otherId != game.requestorId) { throw new IllegalStateException("other wasn't requestee or requestorId") }
     responseCheck
     myQuizStatusCheck
     otherQuizStatusCheck
     myAnswerStatusCheck
     otherAnswerStatusCheck
   }
}

// Game in requested state (requestor can still create a quiz while waiting)
case class RequestedNoQuiz(game: Game, meId : UserId, otherId : UserId) extends GameMask with Requested with MyQuizUnfinished with OtherQuizUnfinished with BothStillAnswering { checks }
case class RequestedQuizDone(game: Game, meId : UserId, otherId : UserId) extends GameMask with Requested with MyQuizFinished with OtherQuizUnfinished with BothStillAnswering { checks }
// Game was rejected (but requestor could have made a quiz before it was rejected)
case class RejectedNoQuiz(game: Game, meId : UserId, otherId : UserId) extends GameMask with Rejected with MyQuizUnfinished with OtherQuizUnfinished with BothStillAnswering { checks }
case class RejectedQuizDone(game: Game, meId : UserId, otherId : UserId) extends GameMask with Rejected with MyQuizFinished with OtherQuizUnfinished with BothStillAnswering { checks }
// Game is in progress both players are making quizzes
case class AcceptedMeNoQuizOtherNoQuiz(game: Game, meId : UserId, otherId : UserId) extends GameMask with Accepted with MyQuizUnfinished with OtherQuizUnfinished with BothStillAnswering { checks }
case class AcceptedMeQuizDoneOtherNoQuiz(game: Game, meId : UserId, otherId : UserId) extends GameMask with Accepted with  MyQuizFinished with OtherQuizUnfinished with BothStillAnswering { checks }
case class AcceptedMeNoQuizOtherQuizDone(game: Game, meId : UserId, otherId : UserId) extends GameMask with Accepted with MyQuizUnfinished with OtherQuizFinished with BothStillAnswering { checks }
// Game is in progress both quizzes have been made and players are answering
case class QuizzesDoneMeAnsOtherAns(game: Game, meId : UserId, otherId : UserId) extends GameMask with Accepted with QuizzesBothDone with BothStillAnswering { checks }
case class QuizzesDoneMeAnsOtherDone(game: Game, meId : UserId, otherId : UserId) extends GameMask with Accepted with QuizzesBothDone with MyStillAnswering with OtherDoneAnswering { checks }
case class QuizzesDoneMeDoneOtherAns(game: Game, meId : UserId, otherId : UserId) extends GameMask with Accepted with  QuizzesBothDone with MyDoneAnswering with OtherStillAnswering { checks }
case class GameDone(game: Game, meId : UserId, otherId : UserId) extends GameMask with Accepted with QuizzesBothDone with BothDoneAnswering { checks }
