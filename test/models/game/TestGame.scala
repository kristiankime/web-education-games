package models.game

import com.artclod.slick.JodaUTC
import models.user.{User}
import play.api.db.slick.Config.driver.simple.Session

object TestGame {

  def finish(game: Game)(implicit session: Session) = {
    // NOTE: This update puts the game in an illegal state (i.e. calling toState will fail) and should just used for testing
    val finishedGame = game.copy(finishedDate = Some(JodaUTC.now))
    Games.update(finishedGame)
    finishedGame
  }

  def score(game: Game)(requestorStudent : Int = 0, requesteeStudent : Int = 0, requestorTeacher : Int = 0, requesteeTeacher : Int = 0) = {
    game.copy(
      requestorStudentPoints = Some(requestorStudent),
      requesteeStudentPoints = Some(requesteeStudent),
      requestorTeacherPoints = Some(requestorTeacher),
      requesteeTeacherPoints = Some(requesteeTeacher)
    )
  }

  def createFinished(requestor: User, requestee : User, requestorStudent : Int = 0, requesteeStudent : Int = 0, requestorTeacher : Int = 0, requesteeTeacher : Int = 0)(implicit session: Session) = {
    val game = score(Games.request(requestor, requestee))(requestorStudent, requesteeStudent, requestorTeacher, requesteeTeacher)
    finish(game)
  }

}
