package models

import models.game.Games
import models.organization.Courses
import models.question.derivative.QuestionSummary
import models.question.derivative.{QuestionDifficulty, Answers, Questions}
import models.support.{CourseId, UserId}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.User

package object user {

  implicit class UserPimped(user: User){

    def settings(implicit session: Session) = UserSettings(user.id)

    def consented(implicit session: Session) = settings match {
      case None => false
      case Some(setting) => setting.consented
    }

    def name(implicit session: Session) = settings match {
      case None => throw new IllegalStateException("Programming error, name() should only be called if the user has settings")
      case Some(setting) => setting.name
    }

    def activeGame(otherId: UserId)(implicit session: Session) = Games.activeGame(user.id, otherId)

    def studentsToPlayWith(courseId: CourseId)(implicit session: Session) = Games.studentsToPlayWith(user.id, courseId)

    def gameRequests(courseId: CourseId)(implicit session: Session) = Games.requests(user.id, courseId)

    def activeGames(courseId: CourseId)(implicit session: Session) = Games.active(user.id, courseId)

    def finishedGames(courseId: CourseId)(implicit session: Session) = Games.finished(user.id, courseId)

    def courses()(implicit session: Session) = Courses(user.id)

    def skillLevel(implicit session: Session) : Double = skillLevelPrivate(Questions.summary(user))

    def skillLevel(asOf: DateTime)(implicit session: Session) : Double = skillLevelPrivate(Questions.summary(user, asOf))

    private def skillLevelPrivate(questionSummaries: List[QuestionSummary]) : Double = {
      val top5 = questionSummaries.filter(_.correct).map(s => QuestionDifficulty(s.mathML)).sortWith( _ > _).take(5)
      if(top5.isEmpty) 0
      else top5.sum.toDouble / top5.size.toDouble
    }
  }

}
