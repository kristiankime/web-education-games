package models.tournament

import models.game.table.gamesTable
import models.quiz.table._
import models.support._
import play.api.db.slick.Config.driver.simple._
import scala.language.postfixOps
import models.user.table.userSettingsTable
import models.quiz.Correct2Short

object Tournaments {

  // ============ Generic Ranking Core ===========
  case class Rankings[M](ranks: List[Rank[M]], user: Option[Rank[M]])

  case class Rank[M](id:UserId, name: String, metric: M, index: Int)

  private def rankingsFor[M](id:UserId, size: Int, ranks:  List[Rank[M]]) = {
    val findUserRank = ranks.find(_.id == id)
    val userRank = findUserRank.flatMap(e => if(e.index > size){ Some(e) } else { None })
    Rankings[M](ranks.take(size), userRank)
  }

  // ============ Student Scores Rankings ===========
  def studentScoresRankingFor(id:UserId, size: Int)(implicit session: Session) = {
    rankingsFor(id, size, studentScoresRankings)
  }

  private def studentScoresRankings(implicit session: Session) = {
    val usersAndAnswers = questionsAnsweredCorrectly
    val user2Difficulty = usersAndAnswers.groupBy(r => (r._1, r._2)).mapValues(_.map(_._3))
    val user2HighestDifficulty = user2Difficulty.mapValues(l => l.sorted(Ordering[Double].reverse).take(5))
    val user2HighScoreAverage = user2HighestDifficulty.mapValues(v => v.sum / v.length)
    val usersAndScores = user2HighScoreAverage.toList.map(e => (e._1._1, e._1._2, e._2)).sortBy(_._3)((Ordering[Double].reverse))
    val rankings = usersAndScores.zipWithIndex.map(v => Rank[Double](v._1._1, v._1._2, v._1._3, v._2))
    rankings
  }

  private def questionsAnsweredCorrectly(implicit session: Session) = {
    val correctAnswers /* one entry per question + user */ = derivativeAnswersTable.filter(_.correct === Correct2Short.T).groupBy(g => (g.ownerId, g.questionId)).map{ case ((ownerId, questionId), group) => (ownerId, questionId) }
    val questionsAnsweredCorrectly = correctAnswers innerJoin derivativeQuestionsTable on (_._2 === _.id)
    val userAndQuestionDifficulty = questionsAnsweredCorrectly.map { r => (r._1._1, r._2.atCreationDifficulty) }.sortBy(r => (r._1, r._2))
    val userNamesAndDifficulty = for { (q, s) <- userAndQuestionDifficulty innerJoin userSettingsTable on (_._1=== _.userId) } yield (q._1, s.name, q._2)
    userNamesAndDifficulty.list
  }

  // ============ Number of Games Completed Rankings ===========
  def completedGamesRankingFor(id:UserId, size: Int)(implicit session: Session) = {
    rankingsFor(id, size, completedGamesRank)
  }

  private def completedGamesRank(implicit session: Session) = {
    val completedGames = numberOfCompletedGamesByPlayer
    val gamesRankings = completedGames.zipWithIndex.map(v => Rank(v._1._1, v._1._2, v._1._3.getOrElse(0), v._2))
    gamesRankings
  }

  private def numberOfCompletedGamesByPlayer(implicit session: Session) = {
    val requstorCounts = gamesTable.filter(_.finishedDate.isNotNull).groupBy(g => g.requestor).map{ case (requestor, group) => (requestor, group.length) }
    val requsteeCounts = gamesTable.filter(_.finishedDate.isNotNull).groupBy(g => g.requestee).map{ case (requestee, group) => (requestee, group.length) }
    val countsUnion = requstorCounts.unionAll(requsteeCounts)
    val counts = countsUnion.groupBy(g => g._1).map{ case (id, group) => (id, group.map(_._2).sum) }
    val joinNames = for { (c, s) <- counts innerJoin userSettingsTable on (_._1 === _.userId) } yield (c._1, s.name, c._2)
    val sorted = joinNames.sortBy(_._2)
    sorted.list
  }

}
