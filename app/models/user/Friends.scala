package models.user

import com.artclod.slick.JodaUTC
import com.google.common.annotations.VisibleForTesting
import models.organization.Courses
import models.organization.table._
import models.support.{CourseId, UserId}
import models.user.table._
import play.api.db.slick.Config.driver.simple._
import service.Access

object Friends {

  def apply(user: UserId, friend: UserId)(implicit session: Session) : Option[Friend] =
    friendsTable.where(f => f.userId === user && f.friendId === friend).firstOption

  def invitation(friendId: UserId)(implicit user: User, session: Session) = {
    val invite = Friend(user.id, friendId, Some(JodaUTC.now), None)
    friendsTable += invite
    invite
  }

  def pendingInvitesFor(user: User)(implicit session: Session) =
    friendsTable.where(f => f.friendId === user.id && f.requestDate.isNotNull && f.acceptDate.isNull).sortBy(_.requestDate).list

  def pendingInvitesBy(user: User)(implicit session: Session) =
    friendsTable.where(f => f.userId === user.id && f.requestDate.isNotNull && f.acceptDate.isNull).sortBy(_.requestDate).list

  def acceptInvitation(invite: Friend)(implicit user: User, session: Session) = {
    if(invite.friendId != user.id) { throw new IllegalStateException("Cannot accept invitation unless user is the invited one") }
    val now = Some(JodaUTC.now)
    val updatedInvite = invite.copy(acceptDate = now)
    findInviteQuery(invite).update(updatedInvite)
    val accept = Friend(user.id, invite.userId, None, now)
    friendsTable += accept
    (updatedInvite, accept)
  }

  def rejectInvitation(invite: Friend)(implicit user: User, session: Session) = {
    if(invite.friendId != user.id) { throw new IllegalStateException("Cannot reject invitation unless user is the invited one") }
    findInviteQuery(invite).delete
  }

  def friends(implicit user: User, session: Session) =
    friendsTable.where(f => f.userId === user.id && f.acceptDate.isNotNull).sortBy(_.acceptDate).list

  def possibleFriends(userId: UserId)(implicit session: Session) =
    usersTable.filterNot(_.userId.inSet(userId :: currentFriendIds(userId))).list

  def isPossibleFriend(friendId: UserId)(implicit user: User, session: Session) =
    !possibleFriends(user.id).contains(friendId)

  def unfriend(invite: Friend)(implicit user: User, session: Session) = {
    if(invite.userId != user.id) { throw new IllegalStateException("Cannot unfriend unless user is the invited one [" + invite + "] user [" + user.id + "]") }
    val responseOp = apply(invite.friendId, invite.userId)
    if(responseOp.isEmpty) { throw new IllegalStateException("There was no 2nd Invite for unfriending " + invite) }
    for(response <- responseOp) {
      findInviteQuery(response).delete
      findInviteQuery(invite).update(invite.copy(acceptDate = None))
    }
  }

  def studentsAndFriends(courseId: CourseId, userId: UserId)(implicit session: Session) = {
    val friends = Set(currentFriendIds(userId): _*)
    Courses.students(courseId).filter(s => friends.contains(s.id))
  }

  // ==== Support
  private def findInviteQuery(invite: Friend)(implicit session: Session) = friendsTable.where(f => f.userId === invite.userId && f.friendId === invite.friendId)

  @VisibleForTesting
  def currentFriendIds(userId: UserId)(implicit session: Session): List[UserId] = {
    val u = (for (f <- friendsTable if f.userId   === userId && f.acceptDate.isNotNull) yield f.friendId)
    val f = (for (f <- friendsTable if f.friendId === userId && f.acceptDate.isNotNull) yield f.userId)
    u.union(f).list
  }



}

