package models.game

import play.api.db.slick.Config.driver.simple.TableQuery

package object table {
  val gamesTable = TableQuery[GamesTable]
}
