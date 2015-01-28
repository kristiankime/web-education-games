package models.user

import models.game.Games
import models.organization.Courses
import models.quiz.answer.result.DerivativeQuestionScores
import models.quiz.question.{QuestionDifficulty, DerivativeQuestions}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import models.support.{CourseId, UserId}
import models.user.table._
import service.Logins
import scala.util.Try

case class UserSetting(userId: UserId, consented: Boolean = true, name: String, allowAutoMatch: Boolean = true, seenHelp: Boolean = false, emailGameUpdates: Boolean = true) {
  val id = userId

  def n = views.html.tag.name(this)

  def nStr = UserSettings.name(name, userId)

  def email(implicit session: Session) = Logins(id).flatMap(_.email)

  /**
   * If we can (and should) send an email to the user returns Some(their_email), otherwise None.
   */
  def maybeSendGameEmail(implicit session: Session) = if(emailGameUpdates){ Logins(id).flatMap(_.email) } else { None }

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

object UserSettings {

  def name(name:String, id: UserId) = name + "-" + id.v


  def create(userSetting: UserSetting)(implicit session: Session) =
    Try(session.withTransaction { userSettingsTable.insert(userSetting); userSetting })

  def update(userSetting: UserSetting)(implicit session: Session) =
    Try(session.withTransaction {userSettingsTable.where(_.userId === userSetting.userId).update(userSetting); userSetting })

  def apply(userId: UserId)(implicit session: Session) : Option[UserSetting] = userSettingsTable.where(_.userId === userId).firstOption

  /**
   * Produces a name that was unique at the time that this call was made.
   * Note that this name may not unique if an update/insert is done later.
   */
  def validName(startingName: String)(implicit session: Session) = {
    userSettingsTable.where(_.name === startingName).firstOption match {
      case None => startingName
      case Some(_) => {
        val similarNames = userSettingsTable.where(_.name like (startingName + "%")).list.map(_.name).toSet

        if(!similarNames(startingName)) startingName
        else {
          var count = 0
          while (similarNames(startingName + count)) { count += 1 }
          startingName + count
        }
      }
    }
  }

}

