package models.user

import com.artclod.slick.JodaUTC
import models.organization.table._
import models.support.{CourseId, UserId}
import models.user.table._
import play.api.db.slick.Config.driver.simple._
import service.Access

object Friends {

  def apply(userId: UserId, friendId: UserId)(implicit session: Session) : Option[Friend] =
    friendsTable.where(f => f.userId === userId && f.friendId === friendId).firstOption

  def possibleFriends(userId: UserId)(implicit session: Session) =
    usersTable.filterNot(_.userId.inSet(currentFriends(userId))).list

  def friends(implicit user: User, session: Session) =
    friendsTable.where(f => (f.userId === user.id || f.friendId === user.id) && f.acceptDate.isNotNull).sortBy(_.acceptDate).list

  def currentFriends(userId: UserId)(implicit session: Session): List[UserId] = {
    val u = (for (f <- friendsTable if f.userId   === userId && f.acceptDate.isNotNull) yield f.friendId)
    val f = (for (f <- friendsTable if f.friendId === userId && f.acceptDate.isNotNull) yield f.userId)
    u.union(f).list
  }

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


}

