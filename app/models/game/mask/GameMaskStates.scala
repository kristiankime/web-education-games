package models.game.mask

import com.artclod.slick.JodaUTC
import models.game.{GameResponseStatus, Game}
import models.support.{QuizId, UserId}
import play.api.db.slick.Config.driver.simple.Session

// ============================
// ====== Request states ======
sealed trait ResponseStatus extends GameSetup {
  def responseCheck : Unit
}

trait Requested extends ResponseStatus {
  override def responseCheck = if(game.response != GameResponseStatus.requested) {throw new IllegalStateException("Game must be in request state")}

  def accept(implicit session: Session) = {
    if (!requestee) throw new IllegalStateException("user [" + meId + "] attempted to reject game but was not requestee [" + meId + "]")
    game.copy(response = GameResponseStatus.accepted)
  }

  def reject(requesteeId : UserId)(implicit session: Session) = {
    if (!requestee) throw new IllegalStateException("user [" + meId + "] attempted to accept game but was not requestee [" + meId + "]")
    game.copy(response = GameResponseStatus.rejected, finishedDate = Some(JodaUTC.now))
  }
}

trait Rejected extends ResponseStatus {
  override def responseCheck = if(game.response != GameResponseStatus.rejected) {throw new IllegalStateException("Game must be in rejected state")}
}

trait Accepted extends ResponseStatus {
  override def responseCheck = if(game.response != GameResponseStatus.accepted) {throw new IllegalStateException("Game must be in accepted state")}
}
// =====================================


// ===================================
// ====== My Quiz States ======
sealed trait MyQuizStatus extends GameSetup {
  def myQuizStatusCheck : Unit
}

trait MyQuizUnfinished extends MyQuizStatus {
  override def myQuizStatusCheck = if(myQuizDone != false) {throw new IllegalStateException("Game must be in My Quiz unfinished state")}

  def finalizeMyQuiz = {
    if(myQuizId.isEmpty) { throw new IllegalStateException("Game must have a my quiz to finalize")}
    if(requestee) { game.copy(requesteeQuizDone = true) } else {  game.copy(requestorQuizDone = true) }
  }
}

trait MyQuizFinished extends MyQuizStatus {
  override def myQuizStatusCheck = {
    if(myQuizId.isEmpty) {throw new IllegalStateException("Game must have a My quiz")}
    if(myQuizDone != true) {throw new IllegalStateException("Game must be in My Quiz done state")}
  }
}

// ====== Other Quiz States ======
sealed trait OtherQuizStatus extends GameSetup {
  def otherQuizStatusCheck : Unit
}

trait OtherQuizUnfinished extends OtherQuizStatus {
  override def otherQuizStatusCheck =  if(otherQuizDone != false) {throw new IllegalStateException("Game must be in Other Quiz not done state")}
}

trait OtherQuizFinished extends OtherQuizStatus {
  override def otherQuizStatusCheck = {
    if(otherQuizId.isEmpty) {throw new IllegalStateException("Game must have a Other quiz")}
    if(otherQuizDone != true) {throw new IllegalStateException("Game must be in Other Quiz done state")}
  }
}

// =====================================
trait QuizzesBothDone extends MyQuizFinished with OtherQuizFinished
// =====================================


// =====================================
// ====== My Answer States ======
trait MyAnswerStatus extends GameSetup {
  def myAnswerStatusCheck : Unit
}

trait MyStillAnswering extends MyAnswerStatus {
  override def myAnswerStatusCheck = if (myFinished != false) { throw new IllegalStateException("My cannot be done answering")  }

  def doneAnswering(implicit session: Session) = {
    val results = otherQuiz.results(me)

    if(requestee)
      game.copy(
        requesteeFinished = true,
        requesteeStudentPoints = Some(results.studentPercent),
        requestorTeacherPoints = Some(results.teacherPercent(mySkill))
      ).maybeUpdateForGameDone
    else
      game.copy(
        requestorFinished = true,
        requestorStudentPoints = Some(results.studentPercent),
        requesteeTeacherPoints = Some(results.teacherPercent(mySkill))
      ).maybeUpdateForGameDone
  }
}

trait MyDoneAnswering extends MyAnswerStatus {
  override def myAnswerStatusCheck = if (myFinished != true) { throw new IllegalStateException("My must be done answering")  }
}

// ====== Other Answer States ======
trait OtherAnswerStatus extends GameSetup {
  def otherAnswerStatusCheck : Unit
}

trait OtherStillAnswering extends OtherAnswerStatus {
  override def otherAnswerStatusCheck = if (otherFinished != false) { throw new IllegalStateException("Other cannot be done answering")  }
}

trait OtherDoneAnswering extends OtherAnswerStatus {
  override def otherAnswerStatusCheck = if (otherFinished != true) { throw new IllegalStateException("Other must be done answering")  }
}
// =====================================
trait BothStillAnswering extends OtherStillAnswering with MyStillAnswering

trait BothDoneAnswering extends OtherDoneAnswering with MyDoneAnswering
// =====================================
