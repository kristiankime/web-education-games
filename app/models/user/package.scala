//<<<<<<< Updated upstream
//package models
//
//import models.game.Games
//import models.organization.Courses
//import models.quiz.answer.result.DerivativeQuestionScores
//import models.quiz.question.{QuestionDifficulty, DerivativeQuestions}
//import models.support.{CourseId, UserId}
//import org.joda.time.DateTime
//import play.api.db.slick.Config.driver.simple._
//import service.Login
//
//package object user {
//
//  implicit class UserPimped(user: Login){
//
//    def settingsOp(implicit session: Session) = UserSettings(user.id)
//
//    def consented(implicit session: Session) = settingsOp match {
//      case None => false
//      case Some(setting) => setting.consented
//    }
//
//    def settings(implicit session: Session) : UserSetting = UserSettings(user.id) match {
//      case None => throw new IllegalStateException("Programming error user has no settings")
//      case Some(setting) => setting
//    }
//
//    def name(implicit session: Session) = settingsOp match {
//      case None => throw new IllegalStateException("Programming error, name method should only be called if the user has settings")
//      case Some(setting) => UserFull.name(setting.name, user.id)
//    }
//
//    /**
//     * If we can (and should) send an email to the user returns Some(their_email), otherwise None.
//     */
//    def maybeSendGameEmail(implicit session: Session) = settingsOp.flatMap(s => if(s.emailGameUpdates){ user.email } else None)
//
//    def activeGame(otherId: UserId)(implicit session: Session) = Games.activeGame(user.id, otherId)
//
//    def studentsToPlayWith(courseId: CourseId)(implicit session: Session) = Games.studentsToPlayWith(user.id, courseId)
//
//    def gameRequests(courseId: CourseId)(implicit session: Session) = Games.requests(user.id, courseId)
//
//    def activeGames(courseId: CourseId)(implicit session: Session) = Games.active(user.id, courseId)
//
//    def finishedGames(courseId: CourseId)(implicit session: Session) = Games.finished(user.id, courseId)
//
//    def courses()(implicit session: Session) = Courses(user.id)
//
//    def studentSkillLevel(implicit session: Session) : Double = studentSkillLevelPrivate(DerivativeQuestions.summary(user))
//
//    def studentSkillLevel(asOf: DateTime)(implicit session: Session) : Double = studentSkillLevelPrivate(DerivativeQuestions.summary(user, asOf))
//
//    private def studentSkillLevelPrivate(questionSummaries: List[DerivativeQuestionScores]) : Double = {
//      val top5 = questionSummaries.filter(_.correct).map(s => QuestionDifficulty(s.mathML)).sortWith( _ > _).take(5)
//      math.max(1d,
//        if(top5.isEmpty) 1d
//        else top5.sum.toDouble / top5.size.toDouble
//      )
//    }
//  }
//
//}
//=======
////package models
////
////import models.game.Games
////import models.organization.Courses
////import models.quiz.answer.result.DerivativeQuestionScores
////import models.quiz.question.{QuestionDifficulty, DerivativeQuestions}
////import models.support.{CourseId, UserId}
////import org.joda.time.DateTime
////import play.api.db.slick.Config.driver.simple._
////import service.Login
////
////package object user {
////
////  implicit class UserPimped(user: Login){
////
////    def settingsOp(implicit session: Session) = UserSettings(user.id)
////
////    def consented(implicit session: Session) = settingsOp match {
////      case None => false
////      case Some(setting) => setting.consented
////    }
////
////    def settings(implicit session: Session) : UserSetting = UserSettings(user.id) match {
////      case None => throw new IllegalStateException("Programming error user has no settings")
////      case Some(setting) => setting
////    }
////
////    def name(implicit session: Session) = settingsOp match {
////      case None => throw new IllegalStateException("Programming error, name method should only be called if the user has settings")
////      case Some(setting) => setting.name
////    }
////
////    /**
////     * If we can (and should) send an email to the user returns Some(their_email), otherwise None.
////     */
////    def maybeSendGameEmail(implicit session: Session) = settingsOp.flatMap(s => if(s.emailGameUpdates){ user.email } else None)
////
////    def activeGame(otherId: UserId)(implicit session: Session) = Games.activeGame(user.id, otherId)
////
////    def studentsToPlayWith(courseId: CourseId)(implicit session: Session) = Games.studentsToPlayWith(user.id, courseId)
////
////    def gameRequests(courseId: CourseId)(implicit session: Session) = Games.requests(user.id, courseId)
////
////    def activeGames(courseId: CourseId)(implicit session: Session) = Games.active(user.id, courseId)
////
////    def finishedGames(courseId: CourseId)(implicit session: Session) = Games.finished(user.id, courseId)
////
////    def courses()(implicit session: Session) = Courses(user.id)
////
////    def studentSkillLevel(implicit session: Session) : Double = studentSkillLevelPrivate(DerivativeQuestions.summary(user))
////
////    def studentSkillLevel(asOf: DateTime)(implicit session: Session) : Double = studentSkillLevelPrivate(DerivativeQuestions.summary(user, asOf))
////
////    private def studentSkillLevelPrivate(questionSummaries: List[DerivativeQuestionScores]) : Double = {
////      val top5 = questionSummaries.filter(_.correct).map(s => QuestionDifficulty(s.mathML)).sortWith( _ > _).take(5)
////      math.max(1d,
////        if(top5.isEmpty) 1d
////        else top5.sum.toDouble / top5.size.toDouble
////      )
////    }
////  }
////
////}
//>>>>>>> Stashed changes
