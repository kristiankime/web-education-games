package models.organization

import com.artclod.slick.JodaUTC
import models.game.{Games, Game}
import play.api.db.slick.Config.driver.simple.Session

object TestGame {


  def finish(game: Game)(implicit session: Session) = {
    // NOTE: This update puts the game in an illegal state (i.e. calling toState will fail) and should just used for testing
    val finishedGame = game.copy(finishedDate = Some(JodaUTC.now))
    Games.update(finishedGame)
    finishedGame
  }

}
