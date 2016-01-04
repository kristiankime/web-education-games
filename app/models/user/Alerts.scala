package models.user

import com.artclod.slick.JodaUTC
import models.game.mask.{MyDoneAnswering, GameMask, MyStillAnswering, MyQuizFinished}
import models.user.table.{alertTables, gameCompletedAlertsTable}
import models.support.{UserId, QuizId}
import play.api.templates.Html
import play.api.db.slick.Config.driver.simple._

object Alerts {

  def alertsHtml(user: User)(implicit session: Session) : Option[Html] = {
    val alerts = activeAlerts(user.id)
    if(alerts.isEmpty) {
      None
    } else {
      markAlertsSeen(alerts)
      Some(views.html.alert.alerts(alerts)(user, session))
    }
  }

  def activeAlerts(userId: UserId)(implicit session: Session) : List[Alert] = {
    val alerts = alertTables.->(
      _.filter(a => a.recipientId === userId && a.seen === false).list)
    //      .toList[Alert](a: GameCompletedAlert => a.asInstanceOf[List[Alert]])

    alerts
  }

  def gameAlert(mask: GameMask)(implicit session: Session) : Unit =
    mask match {
      case m : MyDoneAnswering => (gameCompletedAlertsTable returning gameCompletedAlertsTable.map(_.id)) += GameCompletedAlert(null, m.otherId, false, JodaUTC.now, mask.game.id, mask.otherTeacherPoints, mask.otherStudentPoints, m.otherQuizId.get)
    }

  def markAlertsSeen(alerts: List[Alert])(implicit session: Session) : Unit =
    for(alert <- alerts) { markAlertSeen(alert) }

  def markAlertSeen(alert: Alert)(implicit session: Session) : Unit =
    alert match  {
      case a: GameCompletedAlert => gameCompletedAlertsTable.where(_.id === alert.id).update(a.copy(seen=true))
    }

}
