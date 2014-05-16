import models.support.{HasAccess, Secured}

package object service {

  implicit class EitherHasAccess(e: Either[HasAccess, HasAccess]) {
    def access(implicit user: User, session: scala.slick.session.Session) = e match {
      case Left(s) => s.access
      case Right(s) => s.access
    }
  }

  implicit class OptionAccessPimped(val v: Option[Access]) {
    def toAccess = Access(v)
  }

  implicit class SecuredPimped(val v: Secured) {
    def toAccess(implicit user: User, session: scala.slick.session.Session) = v.access
  }

  implicit object AccessOrdering extends Ordering[Access] {
    def compare(x: Access, y: Access): Int = x.compare(y)
  }

}