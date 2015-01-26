package models.game

import com.artclod.slick.JodaUTC
import play.api.db.slick.Config.driver.simple.Session
import service.Login

object TestGame {

  def finish(game: Game)(implicit session: Session) = {
    // NOTE: This update puts the game in an illegal state (i.e. calling toState will fail) and should just used for testing
    val finishedGame = game.copy(finishedDate = Some(JodaUTC.now))
    Games.update(finishedGame)
    finishedGame
  }

  def score(game: Game)(requestorStudent : Double = 0d, requesteeStudent : Double = 0d, requestorTeacher : Double = 0d, requesteeTeacher : Double = 0d) = {
    game.copy(
      requestorStudentPoints = Some(requestorStudent),
      requesteeStudentPoints = Some(requesteeStudent),
      requestorTeacherPoints = Some(requestorTeacher),
      requesteeTeacherPoints = Some(requesteeTeacher)
    )
  }

  def createFinished(requestor: Login, requestee : Login, requestorStudent : Double = 0d, requesteeStudent : Double = 0d, requestorTeacher : Double = 0d, requesteeTeacher : Double = 0d)(implicit session: Session) = {
    val game = score(Games.request(requestor, requestee))(requestorStudent, requesteeStudent, requestorTeacher, requesteeTeacher)
    finish(game)
  }

}
