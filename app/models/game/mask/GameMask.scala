package models.game.mask

import controllers.game.GamesRequestorController
import models.game.Game
import models.support.{QuestionId, GameId, QuizId, UserId}
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

  def mySkill =                                   if(requestee) { game.requesteeSkill          } else { game.requestorSkill    }
  def myQuizId      : Option[QuizId] =            if(requestee) { game.requesteeQuizId         } else { game.requestorQuizId   }
  def myQuizDone    : Boolean =                   if(requestee) { game.requesteeQuizDone       } else { game.requestorQuizDone }
  def myQuizOp(implicit session: Session) =       if(requestee) { game.requesteeQuiz           } else { game.requestorQuiz     }
  def myQuiz(implicit session: Session) =         myQuizOp.get // LATER move to appropriate GameMaskState
  def myQuizAnswered(implicit session: Session) = if(requestee) { game.requesteeQuizIfAnswered } else { game.requestorQuizIfAnswered }
  def myFinished: Boolean =                       if(requestee) { game.requesteeFinished       } else { game.requestorFinished }
  def myStudentPointsOp =                         if(requestee) { game.requesteeStudentPoints  } else { game.requestorStudentPoints }
  def myStudentPoints =                           myStudentPointsOp.getOrElse(0) // LATER move to appropriate GameMaskState
  def myTeacherPointsOp =                         if(requestee) { game.requesteeTeacherPoints  } else { game.requestorTeacherPoints }
  def myTeacherPoints =                           myTeacherPointsOp.getOrElse(0) // LATER move to appropriate GameMaskState

  def otherSkill =                                   if(requestee) { game.requestorSkill          } else { game.requesteeSkill    }
  def otherQuizId   : Option[QuizId] =               if(requestee) { game.requestorQuizId         } else { game.requesteeQuizId   }
  def otherQuizDone : Boolean =                      if(requestee) { game.requestorQuizDone       } else { game.requesteeQuizDone }
  def otherQuizOp(implicit session: Session) =       if(requestee) { game.requestorQuiz           } else { game.requesteeQuiz     }
  def otherQuiz(implicit session: Session) =         otherQuizOp.get // LATER move to appropriate GameMaskState
  def otherQuizAnswered(implicit session: Session) = if(requestee) { game.requestorQuizIfAnswered } else { game.requesteeQuizIfAnswered }
  def otherFinished: Boolean =                       if(requestee) { game.requestorFinished       } else { game.requesteeFinished }
  def otherStudentPointsOp =                         if(requestee) { game.requestorStudentPoints  } else { game.requesteeStudentPoints }
  def otherStudentPoints =                           otherStudentPointsOp.getOrElse(0) // LATER move to appropriate GameMaskState
  def otherTeacherPointsOp =                         if(requestee) { game.requestorTeacherPoints  } else { game.requesteeTeacherPoints }
  def otherTeacherPoints =                           otherTeacherPointsOp.getOrElse(0) // LATER move to appropriate GameMaskState

  // ==============================================================================================================
  // Play MVC calls which need to change based on requestor/requestee
  // ==============================================================================================================

  // ======================
  // Create a Quiz/Question
  // ======================

  // add Question
  def addQuestion : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.addQuestion(game.id) }
    else {          controllers.game.routes.GamesRequestorController.addQuestion(game.id) }

  // create Question
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

  def createPolynomialZoneQuestionCall : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.createPolynomialZoneQuestion(game.id) }
    else {          controllers.game.routes.GamesRequestorController.createPolynomialZoneQuestion(game.id) }

  def createMultipleChoiceQuestionCall : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.createMultipleChoiceQuestion(game.id) }
    else {          controllers.game.routes.GamesRequestorController.createMultipleChoiceQuestion(game.id) }

  def createMultipleFunctionQuestionCall : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.createMultipleFunctionQuestion(game.id) }
    else {          controllers.game.routes.GamesRequestorController.createMultipleFunctionQuestion(game.id) }

  // Finalize Quiz
  def finalizeCreatedQuiz : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.finalizeCreatedQuiz(game.id) }
    else {          controllers.game.routes.GamesRequestorController.finalizeCreatedQuiz(game.id) }

  // Remove a questions,
  def removeQuestionCall : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.removeQuestion(game.id) }
    else {          controllers.game.routes.GamesRequestorController.removeQuestion(game.id) }

  // ========================
  // Answering Quiz/Question
  // ========================
  def answerDerivativeQuestion(questionId: QuestionId) : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.answerDerivativeQuestion(game.id, questionId) }
    else {          controllers.game.routes.GamesRequestorController.answerDerivativeQuestion(game.id, questionId) }

  def answerDerivativeGraphQuestion(questionId: QuestionId) : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.answerDerivativeGraphQuestion(game.id, questionId) }
    else {          controllers.game.routes.GamesRequestorController.answerDerivativeGraphQuestion(game.id, questionId) }

  def answerGraphMatchQuestion(questionId: QuestionId) : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.answerGraphMatchQuestion(game.id, questionId) }
    else {          controllers.game.routes.GamesRequestorController.answerGraphMatchQuestion(game.id, questionId) }

  def answerTangentQuestion(questionId: QuestionId) : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.answerTangentQuestion(game.id, questionId) }
    else {          controllers.game.routes.GamesRequestorController.answerTangentQuestion(game.id, questionId) }

  def answerPolynomialZoneQuestion(questionId: QuestionId) : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.answerPolynomialZoneQuestion(game.id, questionId) }
    else {          controllers.game.routes.GamesRequestorController.answerPolynomialZoneQuestion(game.id, questionId) }

  def answerMultipleChoiceQuestion(questionId: QuestionId) : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.answerMultipleChoiceQuestion(game.id, questionId) }
    else {          controllers.game.routes.GamesRequestorController.answerMultipleChoiceQuestion(game.id, questionId) }

  def answerMultipleFunctionQuestion(questionId: QuestionId) : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.answerMultipleFunctionQuestion(game.id, questionId) }
    else {          controllers.game.routes.GamesRequestorController.answerMultipleFunctionQuestion(game.id, questionId) }


  // Finalize Answers
  def finalizeAnsweringQuiz : play.api.mvc.Call =
    if(requestee) { controllers.game.routes.GamesRequesteeController.finalizeAnswers(game.id) }
    else {          controllers.game.routes.GamesRequestorController.finalizeAnswers(game.id) }

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
case class ResponseRequiredOtherQuiz(game: Game, meId : UserId, otherId : UserId) extends GameMask with NeedToRespond with MyQuizUnfinished with OtherQuizFinished with BothStillAnswering { checks }
// Game in requested state (requestor can still create a quiz while waiting)
case class RequestedNoQuiz(game: Game, meId : UserId, otherId : UserId) extends GameMask with AwaitingResponse with MyQuizUnfinished with OtherQuizUnfinished with BothStillAnswering { checks }
case class RequestedQuizDone(game: Game, meId : UserId, otherId : UserId) extends GameMask with AwaitingResponse with MyQuizFinished with OtherQuizUnfinished with BothStillAnswering { checks }
// Game was rejected (but requestor could have made a quiz before it was rejected)
case class RejectedNoQuiz(game: Game, meId : UserId, otherId : UserId) extends GameMask with Rejected with MyQuizUnfinished with OtherQuizUnfinished with BothStillAnswering { checks }
case class RejectedMeQuizDone(game: Game, meId : UserId, otherId : UserId) extends GameMask with Rejected with MyQuizFinished with OtherQuizUnfinished with BothStillAnswering { checks }
case class RejectedOtherQuizDone(game: Game, meId : UserId, otherId : UserId) extends GameMask with Rejected with MyQuizUnfinished with OtherQuizFinished with BothStillAnswering { checks }
// Game is in progress both players are making quizzes
case class AcceptedMeNoQuizOtherNoQuiz(game: Game, meId : UserId, otherId : UserId) extends GameMask with Accepted with MyQuizUnfinished with OtherQuizUnfinished with BothStillAnswering { checks }
case class AcceptedMeQuizDoneOtherNoQuiz(game: Game, meId : UserId, otherId : UserId) extends GameMask with Accepted with  MyQuizFinished with OtherQuizUnfinished with BothStillAnswering { checks }
case class AcceptedMeNoQuizOtherQuizDone(game: Game, meId : UserId, otherId : UserId) extends GameMask with Accepted with MyQuizUnfinished with OtherQuizFinished with BothStillAnswering { checks }
// Game is in progress both quizzes have been made and players are answering
case class QuizzesDoneMeAnsOtherAns(game: Game, meId : UserId, otherId : UserId) extends GameMask with Accepted with QuizzesBothDone with BothStillAnswering { checks }
case class QuizzesDoneMeAnsOtherDone(game: Game, meId : UserId, otherId : UserId) extends GameMask with Accepted with QuizzesBothDone with MyStillAnswering with OtherDoneAnswering { checks }
case class QuizzesDoneMeDoneOtherAns(game: Game, meId : UserId, otherId : UserId) extends GameMask with Accepted with  QuizzesBothDone with MyDoneAnswering with OtherStillAnswering { checks }
case class GameDone(game: Game, meId : UserId, otherId : UserId) extends GameMask with Accepted with QuizzesBothDone with BothDoneAnswering { checks }
