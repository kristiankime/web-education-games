package models.user

import models.game.Games
import models.organization.Courses
import models.quiz.answer.result.DerivativeQuestionScores
import models.quiz.question.{QuestionDifficulty, DerivativeQuestions}
import models.support.{CourseId, UserId}
import org.joda.time.DateTime
import service.{Login}
import play.api.db.slick.Config.driver.simple.Session

case class UserFull(user: service.Login, settings: UserSetting) {

//  def id = user.id
//
//  def n = settings.n
//
//  def nStr = UserFull.name(settings.name, user.id)

  /**
   * If we can (and should) send an email to the user returns Some(their_email), otherwise None.
   */
  def maybeSendGameEmail(implicit session: Session) = if(settings.emailGameUpdates){ user.email } else { None }

}

object UserFull {
  def apply(id: UserId)(implicit session: Session): Option[UserFull] = Logins(id).map(l => UserFull.apply(l))


  def apply(user: Login)(implicit session: Session): UserFull = UserSettings(user.id) match {
    case None => throw new IllegalStateException("Programming error user has no settings")
    case Some(setting) => UserFull(user, setting)
  }

  def apply(setting: UserSetting)(implicit session: Session): UserFull = Logins(setting.userId) match {
    case None => throw new IllegalStateException("Programming error user has no login")
    case Some(login) => UserFull(login, setting)
  }

  def name(name:String, id: UserId) = name + "-" + id.v
}
