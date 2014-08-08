package models.organization

import com.artclod.slick.JodaUTC
import org.joda.time.DateTime

object TestOrganization {
  def apply(name: String = "organization",
            creationDate: DateTime = JodaUTC.zero,
            updateDate: DateTime = JodaUTC.zero) = Organization(null, name, creationDate, updateDate)
}
