package models.user

import models.user.table.alertTables
import models.support.{UserId, QuizId}
import play.api.templates.Html
import play.api.db.slick.Config.driver.simple._

object Alerts {

  def alertsHtml(user: User)(implicit session: Session) : Option[Html] = {
    val alerts = activeAlerts(user.id)
    if(alerts.isEmpty) {
      None
    } else {
      Some(views.html.alert.alerts(alerts)(user, session))
    }
  }

  def activeAlerts(userId: UserId)(implicit session: Session) : List[Alert] =
     alertTables.->(
      _.filter(a => a.recipientId === userId && a.seen === false).list)
//      .toList[Alert](a: GameCompletedAlert => a.asInstanceOf[List[Alert]])

}
