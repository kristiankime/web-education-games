import models.support.Secured
import scala.slick.session.Session

package object service {

	implicit object AccessOrdering extends Ordering[Access] {
		def compare(x: Access, y: Access): Int = x.compare(y)
	}
	
	implicit class OptionAccessPimped(val v: Option[Access]) {
		def toAccess = Access(v)
	}
	
	implicit class SecuredPimped(val v: Secured) {
		def toAccess(implicit user: User, session :Session) = v.access
	}
	
}