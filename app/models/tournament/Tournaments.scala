package models.tournament

import com.artclod.slick.NumericBoolean
import com.google.common.annotations.VisibleForTesting
import models.game.table.gamesTable
import models.quiz.table._
import models.support._
import models.user.Users
import models.user.table.usersTable
import play.api.db.slick.Config.driver.simple._

import scala.language.postfixOps

case class Rankings[M](ranks: List[Rank[M]], user: Option[Rank[M]])

case class Rank[M](id:UserId, name: String, metric: M, index: Int)

object RankList{
  def apply[M](list: List[(UserId, String, M)]) = null
}

object Tournaments {

  // ============ Generic Ranking Core ===========
  private def rankings[M](list: List[(UserId, String, M)]) =
    list.zipWithIndex.map { case ((userId, name, metric), index) => Rank[M](userId, Users.nameDisplay(name, userId), metric, index + 1) }

  private def rankingsFor[M](id: UserId, size: Int, ranks: List[Rank[M]]) = {
    val findUserRank = ranks.find(_.id == id)
    val userRank = findUserRank.flatMap(e => if (e.index > size) {
      Some(e)
    } else {
      None
    })
    Rankings[M](ranks.take(size), userRank)
  }

  // ============ Student Scores Rankings ===========
  def studentScoresRankingFor(id: UserId, size: Int)(implicit session: Session) =
    rankingsFor(id, size, rankings(studentScoresRankings))

  @VisibleForTesting
  protected[tournament] def studentScoresRankings(implicit session: Session) = {
    val usersAndAnswers = questionsAnsweredCorrectly
    val user2Difficulty = usersAndAnswers.groupBy(r => (r._1, r._2)).mapValues(_.map(_._3))
    val user2HighestDifficulty = user2Difficulty.mapValues(l => l.sorted(Ordering[Double].reverse).take(5))
    val user2HighScoreAverage = user2HighestDifficulty.mapValues(v => v.sum / v.length)
    val usersAndScores = user2HighScoreAverage.toList.map(e => (e._1._1, e._1._2, e._2)).sortBy(_._3)((Ordering[Double].reverse))
    usersAndScores
  }

  private def questionsAnsweredCorrectly(implicit session: Session) = {
    val correctAnswers /* one entry per question + user */ = derivativeAnswersTable.filter(_.correct === NumericBoolean.T).groupBy(g => (g.ownerId, g.questionId)).map { case ((ownerId, questionId), group) => (ownerId, questionId)}
    val questionsAnsweredCorrectly = correctAnswers innerJoin derivativeQuestionsTable on (_._2 === _.id)
    val userAndQuestionDifficulty = questionsAnsweredCorrectly.map { r => (r._1._1, r._2.atCreationDifficulty)}.sortBy(r => (r._1, r._2))
    val userNamesAndDifficulty = for {(q, s) <- userAndQuestionDifficulty innerJoin usersTable on (_._1 === _.userId)} yield (q._1, s.name, q._2)
    userNamesAndDifficulty.list
  }

  // ============ Number of Games Completed Rankings ===========
  def completedGamesRankingFor(id: UserId, size: Int)(implicit session: Session) =
    rankingsFor(id, size, completedGamesRank)

  @VisibleForTesting
  protected[tournament] def completedGamesRank(implicit session: Session) =
    rankings(numberOfCompletedGamesByPlayer.map(v => v.copy(_3 = v._3.getOrElse(0))))

  private def numberOfCompletedGamesByPlayer(implicit session: Session) = {
    val requstorCounts = gamesTable.filter(_.finishedDate.isNotNull).groupBy(g => g.requestor).map { case (requestor, group) => (requestor, group.length)}
    val requsteeCounts = gamesTable.filter(_.finishedDate.isNotNull).groupBy(g => g.requestee).map { case (requestee, group) => (requestee, group.length)}
    val countsUnion = requstorCounts.unionAll(requsteeCounts)
    val counts = countsUnion.groupBy(g => g._1).map { case (id, group) => (id, group.map(_._2).sum)}
    val joinNames = for {(c, s) <- counts innerJoin usersTable on (_._1 === _.userId)} yield (c._1, s.name, c._2)
    val sorted = joinNames.sortBy(_._3.desc)
    sorted.list
  }

  // ============ Number of Unique Opponents Rankings ===========
  def numberOfUniqueOpponentsRankingFor(id: UserId, size: Int)(implicit session: Session) =
    rankingsFor(id, size, numberOfUniqueOpponentsRank)

  @VisibleForTesting
  protected[tournament] def numberOfUniqueOpponentsRank(implicit session: Session) =
    rankings(numberOfUniqueOpponents)

  private def numberOfUniqueOpponents(implicit session: Session) = {
    val requstorGames = gamesTable.filter(_.finishedDate.isNotNull).map(r => (r.requestor, r.requestee)) //.groupBy(g => g.requestor).map{ case (requestor, group) => (requestor, group. .requestee) }
    val requsteeGames = gamesTable.filter(_.finishedDate.isNotNull).map(r => (r.requestee, r.requestor)) //.groupBy(g => g.requestee).map{ case (requestee, group) => (requestee, group.requestor) }
    val gamesUnion = requstorGames.unionAll(requsteeGames)
    val uniqueOpponents = gamesUnion.groupBy(g => g._1).map { case (player, group) => (player, group.map(_._2).countDistinct)}
    val joinNames = for {(u, s) <- uniqueOpponents innerJoin usersTable on (_._1 === _.userId)} yield (u._1, s.name, u._2)
    val sorted = joinNames.sortBy(_._3.desc)
    sorted.list
  }

  // ============ Sum of Student Score Rankings ===========
  def sumOfStudentScoresRankingFor(id: UserId, size: Int)(implicit session: Session) =
    rankingsFor(id, size, sumOfStudentScoresRank)

  @VisibleForTesting
  protected[tournament] def sumOfStudentScoresRank(implicit session: Session) =
    rankings(sumOfStudentScores.map(v => v.copy(_3 = v._3.getOrElse(0d))))

  private def sumOfStudentScores(implicit session: Session) = {
    val requstorGames = gamesTable.filter(_.finishedDate.isNotNull).map(r => (r.requestor, r.requestorStudentPoints))
    val requsteeGames = gamesTable.filter(_.finishedDate.isNotNull).map(r => (r.requestee, r.requesteeStudentPoints))
    val gamesUnion = requstorGames.unionAll(requsteeGames)
    val sumScores = gamesUnion.groupBy(_._1).map { case (player, group) => (player, group.map(_._2).sum)}
    val joinNames = for {(u, s) <- sumScores innerJoin usersTable on (_._1 === _.userId)} yield (u._1, s.name, u._2)
    val sorted = joinNames.sortBy(_._3.desc)
    sorted.list
  }

  // ============ Sum of Teacher Score Rankings ===========
  def sumOfTeacherScoresRankingFor(id: UserId, size: Int)(implicit session: Session) =
    rankingsFor(id, size, sumOfTeacherScoresRank)

  @VisibleForTesting
  protected[tournament] def sumOfTeacherScoresRank(implicit session: Session) =
    rankings(sumOfTeacherScores.map(v => v.copy(_3 = v._3.getOrElse(0d))))

  private def sumOfTeacherScores(implicit session: Session) = {
    val requstorGames = gamesTable.filter(_.finishedDate.isNotNull).map(r => (r.requestor, r.requestorTeacherPoints))
    val requsteeGames = gamesTable.filter(_.finishedDate.isNotNull).map(r => (r.requestee, r.requesteeTeacherPoints))
    val gamesUnion = requstorGames.unionAll(requsteeGames)
    val sumScores = gamesUnion.groupBy(_._1).map { case (player, group) => (player, group.map(_._2).sum)}
    val joinNames = for {(u, s) <- sumScores innerJoin usersTable on (_._1 === _.userId)} yield (u._1, s.name, u._2)
    val sorted = joinNames.sortBy(_._3.desc)
    sorted.list
  }

}
