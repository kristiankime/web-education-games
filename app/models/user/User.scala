package models.user

import models.game.{Gamification, Games}
import models.organization.Courses
import models.quiz.question._
import models.support.{CourseId, UserId}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.Session
import service.Logins

case class User(id: UserId, consented: Boolean = true, name: String, allowAutoMatch: Boolean = true, seenHelp: Boolean = false, emailUpdates: Boolean = true, lastAccess : DateTime) {

  def nameView = views.html.tag.name(this)

  def nameDisplay = Users.nameDisplay(name, id)

  def email(implicit session: Session) = Logins(id).flatMap(_.email)

  /**
   * If we can (and should) send an email to the user returns Some(their_email), otherwise None.
   */
  def maybeSendEmail(implicit session: Session) = if(emailUpdates){ email } else { None }

  def activeGame(otherId: UserId)(implicit session: Session) = Games.activeGame(id, otherId)

  def studentsToPlayWith(courseId: CourseId)(implicit session: Session) = Games.studentsToPlayWith(id, courseId)

  def friendsToPlayWith(courseId: CourseId)(implicit user: User, session: Session) = Friends.possibleFriendsInCourse(courseId)

  def possibleFriends(implicit user: User, session: Session) = Friends.possibleFriends

  def gameRequests(courseId: CourseId)(implicit session: Session) = Games.requests(id, courseId)

  def activeGames(courseId: CourseId)(implicit session: Session) = Games.active(id, courseId)

  def finishedGames(courseId: CourseId)(implicit session: Session) = Games.finished(id, courseId)

  def courses()(implicit session: Session) = Courses(id)

  def studentSkillLevel(implicit session: Session) : Double = studentSkillLevelPrivate(Questions.results(this))

  def studentSkillLevel(asOf: DateTime)(implicit session: Session) : Double = studentSkillLevelPrivate(Questions.results(this, Some(asOf)))

  private def studentSkillLevelPrivate(questionSummaries: List[QuestionResults]) : Double = {
    val top5 = questionSummaries.filter(_.correct).map(_.question.atCreationDifficulty).sortWith( _ > _).take(5)
    math.max(1d,
      if(top5.isEmpty) 1d
      else top5.sum / top5.size.toDouble
    )
  }

  def studentTotalGamePoints(implicit session: Session) : Int = Games.finished(id).map(_.toMask(this)).map(_.myStudentPoints).sum

  def studentLevel(implicit session: Session) : Int = Gamification.level(studentTotalGamePoints)

  def studentPointsInLevel(implicit session: Session) : Int = Gamification.pointsInLevel(studentTotalGamePoints)

  def teacherTotalGamePoints(implicit session: Session) : Int = Games.finished(id).map(_.toMask(this)).map(_.myTeacherPoints).sum

  def teacherLevel(implicit session: Session) : Int = Gamification.level(teacherTotalGamePoints)

  def teacherPointsInLevel(implicit session: Session) : Int = Gamification.pointsInLevel(teacherTotalGamePoints)

}
