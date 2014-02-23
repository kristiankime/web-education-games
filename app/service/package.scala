import models.id.Secured

package object service {

	implicit object AccessOrdering extends Ordering[Access] {
		def compare(x: Access, y: Access): Int = x.compare(y)
	}
	
	implicit class OptionAccessPimped(val v: Option[Access]) {
		def toAccess = Access(v)
	}
	
	implicit class AccessiblePimped[T](val v: Secured[T]) {
		def toAccess(implicit user: User) = Access(v)
	}
}