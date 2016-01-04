package models.user.table

import com.artclod.slick.JodaUTC._
import models.support.{AlertId, GameId, UserId}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import models.user.Alert

trait AlertsTable[A <: Alert] extends Table[A] {
  def id = column[AlertId]("id", O.PrimaryKey, O.AutoInc)
  def recipientId = column[UserId]("recipient_id")
  def seen = column[Boolean]("consented")
  def creationDate = column[DateTime]("creation_date")
}
