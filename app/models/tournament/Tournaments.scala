package models.tournament

import com.artclod.slick.JodaUTC
import models.game.table.gamesTable
import models.organization.{Course, Courses}
import models.support._
import play.api.db.slick.Config.driver.simple._
import service.User

import scala.language.postfixOps

object Tournaments {

  def numberOfCompletedGamesByPlayer(implicit session: Session) = {
    val requstorCounts = gamesTable.filter(_.finishedDate.isNotNull).groupBy(g => g.requestor).map{ case (requestor, group) => (requestor, group.length) }
    val requsteeCounts = gamesTable.filter(_.finishedDate.isNotNull).groupBy(g => g.requestee).map{ case (requestee, group) => (requestee, group.length) }
    val countsUnion = requstorCounts.unionAll(requsteeCounts)
    val counts = countsUnion.groupBy(g => g._1).map{ case (id, group) => (id, group.map(_._2).sum) }
    val joinNames = for { (c, s) <- counts innerJoin models.user.table.userSettingsTable on (_._1 === _.userId) } yield (c._1, c._2, s.name)
    val limitAndSort = joinNames.sortBy(_._2) //.take(10)

    limitAndSort.list.map(r => Count(r._1, r._2.getOrElse(0), r._3))
  }

  case class Count(id:UserId, count:Int, name: String)

}
