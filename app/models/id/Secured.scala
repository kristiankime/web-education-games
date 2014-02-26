package models.id

import service.User
import scala.slick.session.Session
import service._

trait Secured {

	val owner: UserId
	
	/**
	 * Is the user the owner of this object
	 */
	def ownAccess(implicit user: User) = if (user.id == owner) { Own } else { Non }
	
	/**
	 * Does the user have access to the object other then as an owner.
	 */
	def otherAccess(implicit user: User, session: Session) : Access
	
	/**
	 * Does the user have any kind of access to this object (Owner or Otherwise)
	 */
	def access(implicit user: User, session: Session) = Seq(ownAccess, otherAccess).max
	
}