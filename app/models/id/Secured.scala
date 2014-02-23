package models.id

import service.User
import scala.slick.session.Session
import service.Access

trait Secured[T] {

	val owner: UserId
	
	/**
	 * Is the user the owner of this object
	 */
	def ownAccess(implicit user: User) = Access(Secured.this)
	
	/**
	 * Does the user have access to the object other then as an owner.
	 */
	def otherAccess(t: T)(implicit user: User, session: Session) : Access
	
	/**
	 * Does the user have any kind of access to this object (Owner or Otherwise)
	 */
	def access(t: T)(implicit user: User, session: Session) = Seq(ownAccess, otherAccess(t)).max
	
}