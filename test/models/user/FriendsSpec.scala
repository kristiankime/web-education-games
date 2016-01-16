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
        implicit val user = newFakeUser
        Friends.pendingInvitesFor must beEmpty
      }
    }

    "list nothing if the user invited someone" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        val other = newFakeUser
        Friends.friend(other.id)
        Friends.pendingInvitesFor must beEmpty
      }
    }

    "list invite if someone invited them" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        val other = newFakeUser
        Friends.friend(user.id)(other, session)
        Friends.pendingInvitesFor.map(_.userId) must beEqualTo(List(other.id))
      }
    }

    "list nothing if someone invited them and they accepted" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        val other = newFakeUser
        Friends.friend(user.id)(other, session)
        Friends.friend(other.id)
        Friends.pendingInvitesFor must beEmpty
      }
    }

    "list nothing if someone invited them and they rejected" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        val other = newFakeUser
        Friends.friend(user.id)(other, session)
        Friends.unfriend(other.id)
        Friends.pendingInvitesFor must beEmpty
      }
    }

    "list nothing if someone invited them and they accepted, then unfriended" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        val other = newFakeUser
        Friends.friend(user.id)(other, session)
        Friends.friend(other.id)
        Friends.unfriend(other.id)
        Friends.pendingInvitesFor must beEmpty
      }
    }
  }


  "pendingInvitesBy" should {
    "list nothing if there are no invites" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        Friends.pendingInvitesBy must beEmpty
      }
    }

    "list invite if the user invited someone" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        val other = newFakeUser
        val invite = Friends.friend(other.id)
        Friends.pendingInvitesBy.map(_.friendId) must beEqualTo(List(other.id))
      }
    }

    "list nothing if someone invited them" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        val other = newFakeUser
        Friends.friend(user.id)(other, session)
        Friends.pendingInvitesBy must beEmpty
      }
    }

    "list nothing if the user invited someone and they accepted" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        val other = newFakeUser
        Friends.friend(user.id)(other, session)
        Friends.friend(other.id)
        Friends.pendingInvitesBy must beEmpty
      }
    }

    "list nothing if the user invited someone and they rejected" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        val other = newFakeUser
        Friends.friend(other.id)
        Friends.unfriend(user.id)(other, session)
        Friends.pendingInvitesBy must beEmpty
      }
    }

    "list nothing if the user invited someone and they accepted, then unfriended" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        val other = newFakeUser
        Friends.friend(other.id)
        Friends.friend(user.id)(other, session)
        Friends.unfriend(user.id)(other, session)
        Friends.pendingInvitesBy.map(_.friendId) must beEmpty
      }
    }
  }

  "friends" should {

    "list nothing if there are no invites" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        Friends.friends(user, session) must beEmpty
      }
    }

    "list nothing if there are no accepted invites" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        val friend = newFakeUser
        Friends.friend(friend.id)
        Friends.friends(user, session) must beEmpty
      }
    }

    "list accepted invites (1)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        val friend = newFakeUser
        Friends.friend(friend.id)
        Friends.friend(user.id)(friend, session)
        Friends.friends(user, session).map(_.friendId) must beEqualTo(List(friend.id))
      }
    }

    "list accepted invites (2)" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser

        val friend1 = newFakeUser
        Friends.friend(friend1.id)
        Friends.friend(user.id)(friend1, session)

        val friend2 = newFakeUser
        Friends.friend(friend2.id)
        Friends.friend(user.id)(friend2, session)

        Friends.friends(user, session).map(_.friendId) must beEqualTo(List(friend1.id, friend2.id))
      }
    }

    "Does not list unrelated invites" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser

        val friend1 = newFakeUser
        Friends.friend(friend1.id)
        Friends.friend(user.id)(friend1, session)

        val friend2 = newFakeUser
        Friends.friend(friend2.id)
        Friends.friend(user.id)(friend2, session)

        Friends.friend(friend2.id)(friend1, session)
        Friends.friend(friend1.id)(friend2, session)

        Friends.friends(user, session).map(_.friendId) must beEqualTo(List(friend1.id, friend2.id))
      }
    }
  }
  
}
