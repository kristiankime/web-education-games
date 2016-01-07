package models.user

import models.support.UserId
import org.joda.time.DateTime

case class Friend(userId: UserId, friendId: UserId, requestDate: DateTime, acceptDate: Option[DateTime])
