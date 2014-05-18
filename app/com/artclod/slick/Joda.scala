package com.artclod.slick

import scala.slick.lifted.MappedTypeMapper
import org.joda.time.Duration

object Joda {

  implicit def long2Duration = MappedTypeMapper.base[Duration, Long](
    duration => duration.getMillis,
    long => Duration.millis(long))

}
