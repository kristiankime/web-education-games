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

        val updatedInvite = Friends.acceptInvitation(invitation)(friend, session)

        Friends.friends(user, session) must beEqualTo(List(updatedInvite))
      }
    }

    "list accepted invites (2)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser

        val friend1 = newFakeUser
        val invitation1 = Friends.invitation(friend1.id)(user, session)
        val updatedInvite1 = Friends.acceptInvitation(invitation1)(friend1, session)

        val friend2 = newFakeUser
        val invitation2 = Friends.invitation(friend2.id)(user, session)
        val updatedInvite2 = Friends.acceptInvitation(invitation2)(friend2, session)

        Friends.friends(user, session) must beEqualTo(List(updatedInvite1, updatedInvite2))
      }
    }

    "Does not list unrelated invites" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        val user = newFakeUser

        val friend1 = newFakeUser
        val invitation1 = Friends.invitation(friend1.id)(user, session)
        val updatedInvite1 = Friends.acceptInvitation(invitation1)(friend1, session)

        val friend2 = newFakeUser
        val invitation2 = Friends.invitation(friend2.id)(user, session)
        val updatedInvite2 = Friends.acceptInvitation(invitation2)(friend2, session)

        val friendsFriendInvitation = Friends.invitation(friend2.id)(friend1, session)
        Friends.acceptInvitation(friendsFriendInvitation)(friend2, session)

        Friends.friends(user, session) must beEqualTo(List(updatedInvite1, updatedInvite2))
      }
    }
  }

}
