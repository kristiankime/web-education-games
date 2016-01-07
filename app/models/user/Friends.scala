package models.user

import com.artclod.slick.JodaUTC
import models.support.UserId
import models.user.table._
import play.api.db.slick.Config.driver.simple._

object Friends {

  def invitation(friendId: UserId)(implicit user: User, session: Session) = {
    val invite = Friend(user.id, friendId, JodaUTC.now, None)
    friendsTable += invite
    invite
  }

  def invites(implicit user: User, session: Session) =
    friendsTable.where(f => f.friendId === user.id  && f.acceptDate.isNull).sortBy(_.requestDate).list

  private def findInviteQuery(invite: Friend)(implicit session: Session) = friendsTable.where(f => f.userId === invite.userId && f.friendId === invite.friendId)

  def acceptInvitation(invite: Friend)(implicit user: User, session: Session) = {
    if(invite.friendId != user.id) { throw new IllegalStateException("Cannot accept invitation unless user is the invited one") }
    val updatedInvite = invite.copy(acceptDate = Some(JodaUTC.now))
    findInviteQuery(invite).update(updatedInvite)
    updatedInvite
  }

  def rejectInvitation(invite: Friend)(implicit user: User, session: Session) = {
    if(invite.friendId != user.id) { throw new IllegalStateException("Cannot reject invitation unless user is the invited one") }
    findInviteQuery(invite).delete
  }

  def friends(implicit user: User, session: Session) =
    friendsTable.where(f => (f.userId === user.id || f.friendId === user.id) && f.acceptDate.isNotNull).sortBy(_.acceptDate).list

}

