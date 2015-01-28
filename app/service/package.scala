import models.support.{HasAccess, Secured}
import play.api.db.slick.Config.driver.simple._

package object service {

  implicit class OptionAccessPimped(val v: Option[Access]) {
    def toAccess = Access(v)
  }

}