package models.game

import com.artclod.slick.JodaUTC
import models.organization.{Course, Courses}
import models.game.table.gamesTable
import models.support._
import play.api.db.slick.Config.driver.simple._
import service.User
import models.user.UserPimped
import scala.language.postfixOps

object Games {

  def apply(gameId: GameId)(implicit session: Session): Option[Game] = gamesTable.where(_.id === gameId).firstOption

  // ====== Find players  ======
  def studentsToPlayWith(requestorId: UserId, courseId: CourseId)(implicit session: Session) =
    Courses.studentsExcept(courseId, requestorId).filter(u => activeGame(requestorId, u.id).isEmpty)

  // ====== Request Game ======
  def request(requestor: User, requestee: User, course: Course)(implicit session: Session): Game = {
    val now = JodaUTC.now
    request(Game(requestDate = now, courseId = Some(course.id),
      requestorId = requestor.id, requestorSkill = requestor.studentSkillLevel(now),
      requesteeId = requestee.id, requesteeSkill = requestee.studentSkillLevel(now)))
  }

  def request(requestor: User, requestee: User)(implicit session: Session): Game = {
    val now = JodaUTC.now
    request(Game(requestDate = now,
      requestorId = requestor.id, requestorSkill = requestor.studentSkillLevel(now),
      requesteeId = requestee.id, requesteeSkill = requestee.studentSkillLevel(now)))
  }

  private def request(game: Game)(implicit session: Session): Game = {
    val gameId = (gamesTable returning gamesTable.map(_.id)) += game
    game.copy(id = gameId)
  }

  // ====== Find Games ======
  def activeGame(player1Id: UserId, player2Id: UserId)(implicit session: Session): Option[Game] =
    (gamesTable.where(g => (g.finishedDate isNull) &&
      ((g.requestee === player1Id && g.requestor === player2Id) || (g.requestee === player2Id && g.requestor === player1Id))
    )).firstOption

  // These usually have a "entire universe" and per specific course variant
  def requests(userId: UserId)(implicit session: Session) =
    gamesTable.where(g => g.requestee === userId && g.response === GameResponseStatus.requested).sortBy(_.requestDate.desc).list

  def requests(userId: UserId, courseId: CourseId)(implicit session: Session) =
    gamesTable.where(g => g.requestee === userId && g.response === GameResponseStatus.requested && g.course === courseId).sortBy(_.requestDate.desc).list

  def awaiting(userId: UserId)(implicit session: Session) =
    gamesTable.where(g => g.requestor === userId && g.response === GameResponseStatus.requested).sortBy(_.requestDate.desc).list

  def awaiting(userId: UserId, courseId: CourseId)(implicit session: Session) =
    gamesTable.where(g => g.requestor === userId && g.response === GameResponseStatus.requested && g.course === courseId).sortBy(_.requestDate.desc).list

  def active(userId: UserId)(implicit session: Session): List[Game] =
    gamesTable.where(g => (g.requestee === userId || g.requestor === userId) && (g.finishedDate isNull)).sortBy(_.requestDate.desc).list

  def active(userId: UserId, courseId: CourseId)(implicit session: Session): List[Game] =
    gamesTable.where(g => (g.requestee === userId || g.requestor === userId) && (g.finishedDate isNull) && g.course === courseId).sortBy(_.requestDate.desc).list

  def activeAccepted(userId: UserId)(implicit session: Session): List[Game] =
    gamesTable.where(g => (g.requestee === userId || g.requestor === userId) && g.response === GameResponseStatus.accepted && (g.finishedDate isNull)).sortBy(_.requestDate.desc).list

  def activeAccepted(userId: UserId, courseId: CourseId)(implicit session: Session): List[Game] =
    gamesTable.where(g => (g.requestee === userId || g.requestor === userId) && g.response === GameResponseStatus.accepted && (g.finishedDate isNull) && g.course === courseId).sortBy(_.requestDate.desc).list

  def finished(userId: UserId)(implicit session: Session): List[Game] =
    gamesTable.where(g => (g.requestee === userId || g.requestor === userId) && (g.finishedDate isNotNull)).sortBy(_.requestDate.desc).list

  def finished(userId: UserId, courseId: CourseId)(implicit session: Session): List[Game] =
    gamesTable.where(g => (g.requestee === userId || g.requestor === userId) && (g.finishedDate isNotNull) && g.course === courseId).sortBy(_.requestDate.desc).list

  // ======= Update ======
  def update(game: Game)(implicit session: Session) = gamesTable.where(_.id === game.id).update(game)

}