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
// =====================================


// ===================================
// ====== Requestor Quiz States ======
sealed trait RequestorQuizStatus {
  val game : Game

  def requestorQuizStatusCheck : Unit
}

trait RequestorQuiz extends RequestorQuizStatus {
  override def requestorQuizStatusCheck =  if(game.requestorQuizDone != false) {throw new IllegalStateException("Game must be in Requestor Quiz not done state")}

  def finalizeRequestorQuiz = {
    if(game.requestorQuizId.isEmpty) {throw new IllegalStateException("Game must have a requestor quiz to finalize")}
    game.copy(requestorQuizDone = true)
  }
}

trait RequestorQuizFinished extends RequestorQuizStatus {
  override def requestorQuizStatusCheck = {
    if(game.requestorQuizId.isEmpty) {throw new IllegalStateException("Game must have a requestor quiz")}
    if(game.requestorQuizDone != true) {throw new IllegalStateException("Game must be in requestor Quiz done state")}
  }

  def requestorQuiz(implicit session: Session) = game.requestorQuiz.get
}

// ====== Requestee Quiz States ======
sealed trait RequesteeQuizStatus {
  val game : Game

  def requesteeQuizStatusCheck : Unit
}

trait RequesteeQuiz extends RequesteeQuizStatus {
  override def requesteeQuizStatusCheck = if(game.requesteeQuizId.isEmpty) {throw new IllegalStateException("Game must have Requestee Quiz")}

  def finalizeRequesteeQuiz = {
    if(game.requesteeQuizId.isEmpty) {throw new IllegalStateException("Game must have a Requestee quiz")}
    game.copy(requesteeQuizDone = true)
  }
}

trait RequesteeQuizFinished extends RequesteeQuizStatus {
  override def requesteeQuizStatusCheck = {
    if(game.requesteeQuizId.isEmpty) {throw new IllegalStateException("Game must have a Requestee quiz")}
    if(game.requesteeQuizDone != true) {throw new IllegalStateException("Game must be in Requestee Quiz done state")}
  }

  def requesteeQuiz(implicit session: Session) = game.requesteeQuiz.get
}

// =====================================
trait BothQuizzesDone extends RequesteeQuizFinished with RequestorQuizFinished
// =====================================


// =====================================
// ====== Requestor Answer States ======
trait RequestorAnswerStatus {
  val game : Game

  def requestorAnswerStatusCheck : Unit
}

trait RequestorStillAnswering extends RequestorAnswerStatus with BothQuizzesDone {
  override def requestorAnswerStatusCheck = if (game.requestorFinished != false) { throw new IllegalStateException("Requestor cannot be done answering")  }

  def requestorDoneAnswering = game.copy(requestorFinished = true)
}

trait RequestorDoneAnswering extends RequestorAnswerStatus with BothQuizzesDone {
  override def requestorAnswerStatusCheck = if (game.requestorFinished != true) { throw new IllegalStateException("Requestor must be done answering")  }
}
// ====== Requestee Answer States ======
trait RequesteeAnswerStatus {
  val game : Game

  def requesteeAnswerStatusCheck : Unit
}

trait RequesteeStillAnswering extends RequesteeAnswerStatus with BothQuizzesDone {
  override def requesteeAnswerStatusCheck = if (game.requesteeFinished != false) { throw new IllegalStateException("Requestee cannot be done answering")  }

  def requesteeDoneAnswering = game.copy(requesteeFinished = true)
}

trait RequesteeDoneAnswering extends RequesteeAnswerStatus with BothQuizzesDone {
  override def requesteeAnswerStatusCheck = if (game.requesteeFinished != true) { throw new IllegalStateException("Requestee must be done answering")  }
}
// =====================================
trait BothStillAnswering extends RequesteeStillAnswering with RequestorStillAnswering

trait GameDone extends RequesteeDoneAnswering with RequestorDoneAnswering
// =====================================
