package models.user

import java.util.concurrent.atomic.AtomicLong

import com.artclod.slick.JodaUTC
import models.support.UserId

object UserSettingTest {
  private val id = new AtomicLong(-1000L)

  def apply(consented: Boolean = true,
            name: String = "name",
            allowAutoMatch: Boolean = true,
            seenHelp: Boolean = false,
            emailGameUpdates: Boolean = true) = {
    User(UserId(id.getAndDecrement), consented, name, allowAutoMatch, seenHelp, emailGameUpdates, lastAccess = JodaUTC.zero)
  }
}
