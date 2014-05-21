package com.artclod.slick

import play.api.db.slick.Config.driver.simple._
import org.joda.time.{DateTimeZone, DateTime, Duration}
import java.sql.Date

object Joda {

  implicit def long2Duration = MappedColumnType.base[Duration, Long](
    duration => duration.getMillis,
    long => Duration.millis(long))

  implicit def long2DateTime = MappedColumnType.base[DateTime, Date](
    dateTime => new java.sql.Date(dateTime.getMillis()),
//    date => new DateTime(date, DateTimeZone.UTC))
    date => new DateTime(date))
}
