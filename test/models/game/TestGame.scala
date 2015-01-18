package models.game

import com.artclod.slick.JodaUTC
import play.api.db.slick.Config.driver.simple.Session
import service.User

object TestGame {

  def finish(game: Game)(implicit session: Session) = {
    // NOTE: This update puts the game in an illegal state (i.e. calling toState will fail) and should just used for testing
    val finishedGame = game.copy(finishedDate = Some(JodaUTC.now))
    Games.update(finishedGame)
    finishedGame
  }

  def createFinished(requestor: User, requestee : User)(implicit session: Session) = {
    val game = Games.request(requestor, requestee)
    finish(game)
    game
  }

}
