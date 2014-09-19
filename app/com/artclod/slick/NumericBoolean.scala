package com.artclod.slick

import play.api.db.slick.Config.driver.simple._

/**
 * Most of the aggregation functions in the DB do not work on db booleans.
 * It can be useful to have a boolean treated as a number in the db so max and min for example
 * can be used to determine if there are any of the values are true or false.
 */
object NumericBoolean {

  implicit def boolean2DBNumber = MappedColumnType.base[Boolean, Short](
    bool => if(bool) 1 else 0,
    dbShort => if(dbShort == 1){ true } else if(dbShort == 0) { false } else { throw new IllegalStateException("DB returned [" + dbShort + "] for boolean, must be either 0 or 1")}
  )

}
