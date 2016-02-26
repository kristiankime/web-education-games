package models.user

import com.artclod.slick.JodaUTC
import com.artclod.util.TryUtil._
import models.DBTest
import models.DBTest._
import models.organization.{TestCourse, Courses, TestOrganization, Organizations}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.db.slick._
import play.api.test.{FakeApplication, WithApplication}
import service.{View, Edit}

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


  "friendsToPlayWIth (Course)" should {

    "list nothing if user has no friends" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser
        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = Organizations.create(TestOrganization()).id))
        Friends.friendsToPlayWith(course.id)(user, session) must beEmpty
      }
    }

    "list friend if user has a friend in the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser

        val friend1 = newFakeUser
        Friends.friend(friend1.id)
        Friends.friend(user.id)(friend1, session)

        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = Organizations.create(TestOrganization()).id))
        course.grantAccess(View)(user, session)
        course.grantAccess(View)(friend1, session) // Note friend is in course

        Friends.friendsToPlayWith(course.id)(user, session) must beEqualTo(List(friend1))
      }
    }

    "list nothing if user has a friend who is not in the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      val settings = DB.withSession { implicit session: Session =>
        implicit val user = newFakeUser

        val friend1 = newFakeUser
        Friends.friend(friend1.id)
        Friends.friend(user.id)(friend1, session)

        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = Organizations.create(TestOrganization()).id))
        course.grantAccess(View)(user, session) // Note friend is not added to course

        Friends.friendsToPlayWith(course.id)(user, session) must beEmpty
      }
    }
  }

//  "possibleFriendsInCourse" should {
//
//    "list nothing if the user is the only one in the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
//      val settings = DB.withSession { implicit session: Session =>
//        implicit val user = newFakeUser
//        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = Organizations.create(TestOrganization()).id))
//        course.grantAccess(View)(user, session)
//
//        Friends.possibleFriendsInCourse(course.id)(user, session) must beEmpty
//      }
//    }
//
//  }

    "studentsNotfriendedToPlayWith" should {

      "list students in course who are not friended" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
        val settings = DB.withSession { implicit session: Session =>
          implicit val user = newFakeUser
          val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = Organizations.create(TestOrganization()).id))
          course.grantAccess(View)(user, session)

          val teacher = newFakeUser
          course.grantAccess(Edit)(teacher, session)

          val student1 = newFakeUser
          course.grantAccess(View)(student1, session)
          val student2 = newFakeUser
          course.grantAccess(View)(student2, session)

          val friend1 = newFakeUser
          course.grantAccess(View)(friend1, session)
          Friends.friend(friend1.id)
          Friends.friend(user.id)(friend1, session)

          Set(Friends.studentsNotfriendedToPlayWith(course.id)(user, session): _*) must beEqualTo(Set(student1, student2))
        }
      }

    }

    "engagedFriendIds" should {

      "list any user who the user has invited, or who as invited them" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
        val settings = DB.withSession { implicit session: Session =>
          implicit val user = newFakeUser

          val friend = newFakeUser
          Friends.friend(friend.id)
          Friends.friend(user.id)(friend, session)

          val inivited = newFakeUser
          Friends.friend(inivited.id)

          val invitedBy = newFakeUser
          Friends.friend(user.id)(invitedBy, session)

          val otherUser = newFakeUser // here to ensure unrelated users don't show up

          Set(Friends.engagedFriendIds(user, session) :_*) must beEqualTo(Set(friend.id, inivited.id, invitedBy.id))
        }
      }

    }

}
