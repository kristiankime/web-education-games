package models.tournament

import models.game.table.gamesTable
import models.quiz.table._
import models.support._
import play.api.db.slick.Config.driver.simple._
import scala.language.postfixOps
import models.user.table.userSettingsTable
import models.quiz.Correct2Short

object Tournaments {

  def studentScores(implicit session: Session) = {
    val usersAndAnswers = studentQuestionsAnswered
    val user2Difficulty = usersAndAnswers.groupBy(r => (r._1, r._3)).mapValues(_.map(_._2))
    val user2HighestDifficulty = user2Difficulty.mapValues(l => l.sorted(Ordering[Double].reverse).take(5))
    val user2Score = user2HighestDifficulty.mapValues(v => v.sum / v.length)
    val usersAndScores = user2Score.toList.map(e => Score(e._1._1, e._2, e._1._2)).sortBy(_.score)((Ordering[Double].reverse)).take(5)
    usersAndScores
  }

  def studentQuestionsAnswered(implicit session: Session) = {
    val correctAnswers /* one entry per question + user */ = derivativeAnswersTable.filter(_.correct === Correct2Short.T).groupBy(g => (g.ownerId, g.questionId)).map{ case ((ownerId, questionId), group) => (ownerId, questionId) }
    val questionsAnsweredCorrectly = correctAnswers innerJoin derivativeQuestionsTable on (_._2 === _.id)
    val userAndQuestionDifficulty = questionsAnsweredCorrectly.map { r => (r._1._1, r._2.atCreationDifficulty) }.sortBy(r => (r._1, r._2))
    val userNamesAndDifficulty = for { (q, s) <- userAndQuestionDifficulty innerJoin userSettingsTable on (_._1=== _.userId) } yield (q._1, q._2, s.name)
    userNamesAndDifficulty.list
  }

  case class Score(id:UserId, score:Double, name: String)

  def numberOfCompletedGamesByPlayer(implicit session: Session) = {
    val requstorCounts = gamesTable.filter(_.finishedDate.isNotNull).groupBy(g => g.requestor).map{ case (requestor, group) => (requestor, group.length) }
    val requsteeCounts = gamesTable.filter(_.finishedDate.isNotNull).groupBy(g => g.requestee).map{ case (requestee, group) => (requestee, group.length) }
    val countsUnion = requstorCounts.unionAll(requsteeCounts)
    val counts = countsUnion.groupBy(g => g._1).map{ case (id, group) => (id, group.map(_._2).sum) }
    val joinNames = for { (c, s) <- counts innerJoin userSettingsTable on (_._1 === _.userId) } yield (c._1, c._2, s.name)
    val limitAndSort = joinNames.sortBy(_._2) //.take(10)

    limitAndSort.list.map(r => Count(r._1, r._2.getOrElse(0), r._3))
  }

  case class Count(id:UserId, count:Int, name: String)

}
