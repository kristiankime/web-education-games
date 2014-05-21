package models.support

import play.api.db.slick.Config.driver.simple._
import service._

trait Secured extends HasAccess{

  // LATER change this to ownerId?
	val owner: UserId
	
	/**
	 * Is the user the owner of this object.
	 */
	private def ownerAccess(implicit user: User) = if (user.id == owner) Own else Non
	
	/**
	 * Does the user have access to the object other then as an owner via a direct link table.
	 */
	protected def linkAccess(implicit user: User, session: Session) : Access
	
	/**
	 * Does the user have any kind of direct access to this object (owner or direct link).
	 */
	protected def directAccess(implicit user: User, session: Session) = Seq(ownerAccess, linkAccess).max

}