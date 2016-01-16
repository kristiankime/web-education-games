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

  def friend(friendId: UserId)(implicit user: User, session: Session) = Friends(friendId).friend

  def unfriend(friendId: UserId)(implicit user: User, session: Session) = Friends(friendId).unfriend

  def pendingInvitesFor(implicit user: User, session: Session) =
    friendsTable.where(f => f.friendId === user.id && f.acceptDate.isNull).sortBy(_.requestDate).list

  def pendingInvitesBy(implicit user: User, session: Session) =
    friendsTable.where(f => f.userId === user.id && f.acceptDate.isNull).sortBy(_.requestDate).list

  def friends(implicit user: User, session: Session) =
    friendsTable.where(f => f.userId === user.id && f.acceptDate.isNotNull).sortBy(_.acceptDate).list

  def possibleFriends(implicit user: User, session: Session) =
    usersTable.filterNot(_.userId.inSet(user.id :: possibleFriendInviteIds)).list

  def isPossibleFriend(friendId: UserId)(implicit user: User, session: Session) =
    !possibleFriends.contains(friendId)

  def possibleFriendsInCourse(courseId: CourseId)(implicit user: User, session: Session) = {
    val friends = Set((user.id :: possibleFriendInviteIds): _*)
    Courses.students(courseId).filter(s => friends.contains(s.id))
  }

  // ==== Support Methods
  private def apply(friend: User)(implicit user: User, session: Session) : FriendConnection = apply(friend.id)

  private def apply(friendId: UserId)(implicit user: User, session: Session) : FriendConnection = {
    FriendConnection(
      friendsTable.where(f => f.userId === user.id && f.friendId === friendId).firstOption,
      friendsTable.where(f => f.userId === friendId && f.friendId === user.id).firstOption,
      user, friendId)
  }

  private def findInviteQuery(invite: Friend)(implicit session: Session) = friendsTable.where(f => f.userId === invite.userId && f.friendId === invite.friendId)

  private def possibleFriendInviteIds(implicit user: User, session: Session) = {
    val u = (for (f <- friendsTable if f.userId === user.id) yield f.friendId)
    val f = (for (f <- friendsTable if f.friendId === user.id) yield f.userId)
    u.union(f).list
  }

  // ==== Support Classes and Objects
  private sealed trait FriendConnection {
    val mainOp: Option[Friend]
    val otherOp: Option[Friend]
    val user: User

    def checks() = {
      for (main <- mainOp) {
        if (main.userId != user.id) {
          throw new IllegalStateException("Main must be from the points of view of the user [" + user + "] [" + mainOp + "]")
        }
      }
      for (other <- otherOp) {
        if (other.friendId != user.id) {
          throw new IllegalStateException("Other must be from the points of view of the other [" + user + "] [" + otherOp + "]")
        }
      }
    }

    def friend(implicit session: Session) : Boolean

    def unfriend(implicit session: Session)
  }

  private object FriendConnection {
    def apply(mainOp: Option[Friend], otherOp: Option[Friend], user: User, friendId: UserId) =
      (mainOp, otherOp) match {
        case (None,       None       ) => FriendConnectionNone(user, friendId)
        case (Some(main), None       ) => FriendConnectionMain(main, user, friendId)
        case (None,       Some(other)) => FriendConnectionOther(other, user, friendId)
        case (Some(main), Some(other)) => FriendConnectionBoth(main, other, user, friendId)
      }
  }

  private case class FriendConnectionNone(user: User, friendId: UserId) extends FriendConnection {
    val mainOp = None
    val otherOp = None
    checks()

    def friend(implicit session: Session) = {
      friendsTable += Friend(user.id, friendId, Some(JodaUTC.now), None)
      true;
    }

    def unfriend(implicit session: Session) = {}
  }

  private case class FriendConnectionMain(main: Friend, user: User, friendId: UserId) extends FriendConnection {
    val mainOp = Some(main)
    val otherOp = None
    checks()

    def friend(implicit session: Session) = { false }

    def unfriend(implicit session: Session) = Friends.findInviteQuery(main).delete

  }

  private case class FriendConnectionOther(other: Friend, user: User, friendId: UserId) extends FriendConnection {
    val mainOp = None
    val otherOp = Some(other)
    checks()

    def friend(implicit session: Session) = {
      val now = JodaUTC.now
      val invite = Friend(user.id, friendId, None, Some(now))
      friendsTable += invite
      Friends.findInviteQuery(other).update(other.copy(acceptDate = Some(now)))
      true
    }

    def unfriend(implicit session: Session) = { Friends.findInviteQuery(other).delete }
  }

  private case class FriendConnectionBoth(main: Friend, other: Friend, user: User, friendId: UserId) extends FriendConnection {
    val mainOp = Some(main)
    val otherOp = Some(other)
    checks()

    def friend(implicit session: Session) = { false }

    def unfriend(implicit session: Session) = {
      Friends.findInviteQuery(main).delete
      Friends.findInviteQuery(other).delete
    }
  }

}