package models.game.mask

import models.game.Game
import models.support.{GameId, QuizId, UserId}
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

  def mySkill =                             if(requestee) { game.requesteeSkill    } else { game.requestorSkill    }
  def myQuizId      : Option[QuizId] =      if(requestee) { game.requesteeQuizId   } else { game.requestorQuizId   }
  def myQuizDone    : Boolean =             if(requestee) { game.requesteeQuizDone } else { game.requestorQuizDone }
  def myQuiz(implicit session: Session) =   if(requestee) { game.requesteeQuiz.get } else { game.requestorQuiz.get }
  def myQuizOp(implicit session: Session) = if(requestee) { game.requesteeQuiz     } else { game.requestorQuiz     }
  def myFinished: Boolean =                 if(requestee) { game.requesteeFinished } else { game.requestorFinished }

  def otherSkill =                             if(requestee) { game.requestorSkill    } else { game.requesteeSkill    }
  def otherQuizId   : Option[QuizId] =         if(requestee) { game.requestorQuizId   } else { game.requesteeQuizId   }
  def otherQuizDone : Boolean =                if(requestee) { game.requestorQuizDone } else { game.requesteeQuizDone }
  def otherQuiz(implicit session: Session) =   if(requestee) { game.requestorQuiz.get } else { game.requesteeQuiz.get }
  def otherQuizOp(implicit session: Session) = if(requestee) { game.requestorQuiz     } else { game.requesteeQuiz     }
  def otherFinished: Boolean =                 if(requestee) { game.requestorFinished } else { game.requesteeFinished }

  // Play MVC calls which need to change based on requestor/requestee

  // Remove a questions,
  def removeQuestionCall : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.removeQuestion(game.id) }
    else {          controllers.game.routes.GamesRequestorController.removeQuestion(game.id) }

  // Create a question
  def createDerivativeQuestionCall : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.createDerivativeQuestion(game.id) }
    else {          controllers.game.routes.GamesRequestorController.createDerivativeQuestion(game.id) }

  def createDerivativeGraphQuestionCall : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.createDerivativeGraphQuestion(game.id) }
    else {          controllers.game.routes.GamesRequestorController.createDerivativeGraphQuestion(game.id) }

  def createTangentQuestionCall : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.createTangentQuestion(game.id) }
    else {          controllers.game.routes.GamesRequestorController.createTangentQuestion(game.id) }

  def createGraphMatchQuestionCall : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.createGraphMatchQuestion(game.id) }
    else {          controllers.game.routes.GamesRequestorController.createGraphMatchQuestion(game.id) }

  def finalizeCreatedQuiz : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.finalizeCreatedQuiz(game.id) }
    else {          controllers.game.routes.GamesRequestorController.finalizeCreatedQuiz(game.id) }
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

// Game in requested state (responder needs to respond before doing anything)
case class ResponseRequired(game: Game, meId : UserId, otherId : UserId) extends GameMask with NeedToRespond with MyQuizUnfinished with OtherQuizUnfinished with BothStillAnswering { checks }
// Game in requested state (requestor can still create a quiz while waiting)
case class RequestedNoQuiz(game: Game, meId : UserId, otherId : UserId) extends GameMask with AwaitingResponse with MyQuizUnfinished with OtherQuizUnfinished with BothStillAnswering { checks }
case class RequestedQuizDone(game: Game, meId : UserId, otherId : UserId) extends GameMask with AwaitingResponse with MyQuizFinished with OtherQuizUnfinished with BothStillAnswering { checks }
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
