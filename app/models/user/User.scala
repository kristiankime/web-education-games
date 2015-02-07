package models.user

import models.game.Games
import models.organization.Courses
import models.quiz.answer.result.DerivativeQuestionScores
import models.quiz.question.{DerivativeQuestions, QuestionDifficulty}
import models.support.{CourseId, UserId}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.Session
import service.Logins

case class User(id: UserId, consented: Boolean = true, name: String, allowAutoMatch: Boolean = true, seenHelp: Boolean = false, emailGameUpdates: Boolean = true) {

  def nameView = views.html.tag.name(this)

  def nameDisplay = Users.nameDisplay(name, id)

  def email(implicit session: Session) = Logins(id).flatMap(_.email)

  /**
   * If we can (and should) send an email to the user returns Some(their_email), otherwise None.
   */
  def maybeSendGameEmail(implicit session: Session) = if(emailGameUpdates){ email } else { None }

  def activeGame(otherId: UserId)(implicit session: Session) = Games.activeGame(id, otherId)

  def studentsToPlayWith(courseId: CourseId)(implicit session: Session) = Games.studentsToPlayWith(id, courseId)

  def gameRequests(courseId: CourseId)(implicit session: Session) = Games.requests(id, courseId)

  def activeGames(courseId: CourseId)(implicit session: Session) = Games.active(id, courseId)

  def finishedGames(courseId: CourseId)(implicit session: Session) = Games.finished(id, courseId)

  def courses()(implicit session: Session) = Courses(id)

  def studentSkillLevel(implicit session: Session) : Double = studentSkillLevelPrivate(DerivativeQuestions.summary(this))

  def studentSkillLevel(asOf: DateTime)(implicit session: Session) : Double = studentSkillLevelPrivate(DerivativeQuestions.summary(this, asOf))

  private def studentSkillLevelPrivate(questionSummaries: List[DerivativeQuestionScores]) : Double = {
    val top5 = questionSummaries.filter(_.correct).map(s => QuestionDifficulty(s.mathML)).sortWith( _ > _).take(5)
    math.max(1d,
      if(top5.isEmpty) 1d
      else top5.sum.toDouble / top5.size.toDouble
    )
  }
}
