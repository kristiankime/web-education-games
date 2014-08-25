package models.game

import com.artclod.slick.JodaUTC
import models.support.UserId
import play.api.db.slick.Config.driver.simple.Session

// ============================
// ====== Request states ======
sealed trait GameResponse {
  val game : Game

  def responseCheck : Unit
}

trait GameRequested extends GameResponse {
  override def responseCheck = if(game.response != GameResponseStatus.requested) {throw new IllegalStateException("Game must be in request state")}

  def accept(requesteeId : UserId)(implicit session: Session) = {
    if (game.requestee.id != requesteeId) throw new IllegalStateException("user [" + game.requestee + "] attempted to reject game but was not requestee [" + game.requestee + "]")
    game.copy(response = GameResponseStatus.accepted)
  }

  def reject(requesteeId : UserId)(implicit session: Session) = {
    if (game.requestee.id != requesteeId) throw new IllegalStateException("user [" + game.requestee + "] attempted to accept game but was not requestee [" + game.requestee + "]")
    game.copy(response = GameResponseStatus.rejected, finishedDate = Some(JodaUTC.now))
  }
}

trait GameRejected extends GameResponse {
  override def responseCheck = if(game.response != GameResponseStatus.rejected) {throw new IllegalStateException("Game must be in rejected state")}
}

trait GameAccepted extends GameResponse {
  override def responseCheck = if(game.response != GameResponseStatus.accepted) {throw new IllegalStateException("Game must be in accepted state")}
}

// ===================================
// ====== Requestor Quiz States ======
sealed trait RequestorQuizCreateStatus {
  val game : Game

  def requestorQuizCreateStatusCheck : Unit
}

trait RequestorNoQuiz extends RequestorQuizCreateStatus {
  override def requestorQuizCreateStatusCheck = if(game.requestorQuizId.nonEmpty) {throw new IllegalStateException("Game must not have Requestor Quiz")}
}

trait RequestorQuiz extends RequestorQuizCreateStatus {
  override def requestorQuizCreateStatusCheck = if(game.requestorQuizId.isEmpty) {throw new IllegalStateException("Game must have Requestor Quiz")}

  def requestorQuizId = game.requestorQuizId.get

  def requestorQuiz(implicit session: Session) = game.requestorQuiz.get

  def finalizeRequestorQuiz = game.copy(requestorQuizDone = true)
}

sealed trait RequestorQuizDoneStatus {
  val game : Game

  def requestorQuizDoneStatusCheck : Unit
}

trait RequestorQuizUnfinished extends RequestorQuizDoneStatus with RequestorQuiz{
  override def requestorQuizDoneStatusCheck = if(game.requestorQuizDone != false) {throw new IllegalStateException("Game must be in Requestor Quiz done state")}
}

trait RequestorQuizFinished extends RequestorQuizDoneStatus with RequestorQuiz {
  override def requestorQuizDoneStatusCheck = if(game.requestorQuizDone != true) {throw new IllegalStateException("Game must be in Requestor Quiz done state")}
}

// ====== Requestee Quiz States ======
sealed trait RequesteeQuizCreateStatus {
  val game : Game

  def requesteeQuizCreateStatusCheck : Unit
}

trait RequesteeNoQuiz extends RequesteeQuizCreateStatus {
  override def requesteeQuizCreateStatusCheck = if(game.requesteeQuizId.nonEmpty) {throw new IllegalStateException("Game must not have Requestee Quiz")}
}

trait RequesteeQuiz extends RequesteeQuizCreateStatus {
  override def requesteeQuizCreateStatusCheck = if(game.requesteeQuizId.isEmpty) {throw new IllegalStateException("Game must have Requestee Quiz")}

  def requesteeQuizId = game.requesteeQuizId.get

  def requesteeQuiz(implicit session: Session) = game.requesteeQuiz.get

  def finalizeRequesteeQuiz = game.copy(requesteeQuizDone = true)
}

sealed trait RequesteeQuizDoneStatus {
  val game : Game
  def requesteeQuizDoneStatusCheck : Unit
}

trait RequesteeQuizUnfinished extends RequesteeQuizDoneStatus with RequesteeQuiz {
  override def requesteeQuizDoneStatusCheck = if(game.requesteeQuizDone != false) {throw new IllegalStateException("Game must be in Requestee Quiz done state")}
}

trait RequesteeQuizFinished extends RequesteeQuizDoneStatus with RequesteeQuiz{
  override def requesteeQuizDoneStatusCheck = if(game.requesteeQuizDone != true) {throw new IllegalStateException("Game must be in Requestee Quiz done state")}
}

