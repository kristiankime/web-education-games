package models

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
  }

}
