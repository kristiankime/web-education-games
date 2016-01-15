package models.user

import com.artclod.slick.JodaUTC
import com.artclod.util.TryUtil._
import models.DBTest._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.db.slick._
import play.api.test.{FakeApplication, WithApplication}

@RunWith(classOf[JUnitRunner])
class FriendsSpec extends Specification {

  "pendingInvitesFor" should {
    "list nothing if there are no invites" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        Friends.pendingInvitesFor(user) must beEmpty
      }
    }

    "list nothing if the user invited someone" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        val other = newFakeUser
        Friends.invitation(other.id)(user, session)
        Friends.pendingInvitesFor(user) must beEmpty
      }
    }

    "list invite if someone invited them" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        val other = newFakeUser
        val invite = Friends.invitation(user.id)(other, session)
        Friends.pendingInvitesFor(user) must beEqualTo(List(invite))
      }
    }

    "list nothing if someone invited them and they accepted" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        val other = newFakeUser
        val invite = Friends.invitation(user.id)(other, session)
        Friends.acceptInvitation(invite)(user, session)
        Friends.pendingInvitesFor(user) must beEmpty
      }
    }

    "list nothing if someone invited them and they rejected" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        val other = newFakeUser
        val invite = Friends.invitation(user.id)(other, session)
        Friends.rejectInvitation(invite)(user, session)
        Friends.pendingInvitesFor(user) must beEmpty
      }
    }

    "list invite if someone invited them and they accepted, then unfriended" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        val other = newFakeUser
        val invite = Friends.invitation(user.id)(other, session)
        val (_, accept) = Friends.acceptInvitation(invite)(user, session)
        Friends.unfriend(accept)(user, session)
        Friends.pendingInvitesFor(user) must beEmpty
      }
    }
  }


  "pendingInvitesBy" should {
    "list nothing if there are no invites" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        Friends.pendingInvitesBy(user) must beEmpty
      }
    }

    "list invite if the user invited someone" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        val other = newFakeUser
        val invite = Friends.invitation(other.id)(user, session)
        Friends.pendingInvitesBy(user) must beEqualTo(List(invite))
      }
    }

    "list nothing if someone invited them" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        val other = newFakeUser
        Friends.invitation(user.id)(other, session)
        Friends.pendingInvitesBy(user) must beEmpty
      }
    }

    "list nothing if the user invited someone and they accepted" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        val other = newFakeUser
        val invite = Friends.invitation(other.id)(user, session)
        Friends.acceptInvitation(invite)(other, session)
        Friends.pendingInvitesBy(user) must beEmpty
      }
    }

    "list nothing if the user invited someone and they rejected" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        val other = newFakeUser
        val invite = Friends.invitation(other.id)(user, session)
        Friends.rejectInvitation(invite)(other, session)
        Friends.pendingInvitesBy(user) must beEmpty
      }
    }

    "list invite if the user invited someone and they accepted, then unfriended" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        val other = newFakeUser
        val invite = Friends.invitation(other.id)(user, session)
        val (_, accept) = Friends.acceptInvitation(invite)(other, session)
        Friends.unfriend(accept)(other, session)
        Friends.pendingInvitesBy(user) must beEmpty
      }
    }
  }

  "friends" should {

    "list nothing if there are no invites" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        Friends.friends(user, session) must beEmpty
      }
    }

    "list nothing if there are no accepted invites" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        val friend = newFakeUser
        Friends.invitation(friend.id)(user, session)
        Friends.friends(user, session) must beEmpty
      }
    }

    "list accepted invites (1)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        val friend = newFakeUser
        val invitation = Friends.invitation(friend.id)(user, session)
        val (updatedInvite, _) = Friends.acceptInvitation(invitation)(friend, session)
        Friends.friends(user, session) must beEqualTo(List(updatedInvite))
      }
    }

    "list accepted invites (2)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser

        val friend1 = newFakeUser
        val invitation1 = Friends.invitation(friend1.id)(user, session)
        val (updatedInvite1, _) = Friends.acceptInvitation(invitation1)(friend1, session)

        val friend2 = newFakeUser
        val invitation2 = Friends.invitation(friend2.id)(user, session)
        val (updatedInvite2, _) = Friends.acceptInvitation(invitation2)(friend2, session)

        Friends.friends(user, session) must beEqualTo(List(updatedInvite1, updatedInvite2))
      }
    }

    "Does not list unrelated invites" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser

        val friend1 = newFakeUser
        val invitation1 = Friends.invitation(friend1.id)(user, session)
        val (updatedInvite1, _) = Friends.acceptInvitation(invitation1)(friend1, session)

        val friend2 = newFakeUser
        val invitation2 = Friends.invitation(friend2.id)(user, session)
        val (updatedInvite2, _) = Friends.acceptInvitation(invitation2)(friend2, session)

        val friendsFriendInvitation = Friends.invitation(friend2.id)(friend1, session)
        Friends.acceptInvitation(friendsFriendInvitation)(friend2, session)

        Friends.friends(user, session) must beEqualTo(List(updatedInvite1, updatedInvite2))
      }
    }
  }

  "currentFriendIds" should {

    "list nothing if there are no invites" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser

        Friends.currentFriendIds(user.id) must beEmpty
      }
    }

    "list nothing if there are no accepted invites" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        val friend = newFakeUser

        Friends.invitation(friend.id)(user, session)

        Friends.currentFriendIds(user.id) must beEmpty
      }
    }

    "list accepted invites (1)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser
        val friend = newFakeUser

        val invitation = Friends.invitation(friend.id)(user, session)

        val (updatedInvite, _) = Friends.acceptInvitation(invitation)(friend, session)

        Friends.currentFriendIds(user.id) must beEqualTo(List(friend.id))
      }
    }

    "list accepted invites (2)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser

        val friend1 = newFakeUser
        val invitation1 = Friends.invitation(friend1.id)(user, session)
        val (updatedInvite1, _) = Friends.acceptInvitation(invitation1)(friend1, session)

        val friend2 = newFakeUser
        val invitation2 = Friends.invitation(friend2.id)(user, session)
        val (updatedInvite2, _) = Friends.acceptInvitation(invitation2)(friend2, session)

        Friends.currentFriendIds(user.id) must beEqualTo(List(friend1.id, friend2.id))
      }
    }

    "Does not list unrelated invites" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser

        val friend1 = newFakeUser
        val invitation1 = Friends.invitation(friend1.id)(user, session)
        val (updatedInvite1, _) = Friends.acceptInvitation(invitation1)(friend1, session)

        val friend2 = newFakeUser
        val invitation2 = Friends.invitation(friend2.id)(user, session)
        val (updatedInvite2, _) = Friends.acceptInvitation(invitation2)(friend2, session)

        val friendsFriendInvitation = Friends.invitation(friend2.id)(friend1, session)
        Friends.acceptInvitation(friendsFriendInvitation)(friend2, session)

        Friends.currentFriendIds(user.id) must beEqualTo(List(friend1.id, friend2.id))
      }
    }
  }

}
