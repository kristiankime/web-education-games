package models.organization.assignment

import play.api.db.slick.Config.driver.simple._
import play.api.test._
import play.api.db.slick.DB
import play.api.test.FakeApplication
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import models.DBTest.inMemH2
import models.DBTest.fakeUser
import models.organization._

// TODO check out http://workwithplay.com/blog/2013/06/19/integration-testing/
@RunWith(classOf[JUnitRunner])
class GroupsSpec extends Specification {

  "enrolled" should {

    "return None if the user isn't enrolled in any group" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val admin = fakeUser
        val course = Courses.create(CourseTmpTest(owner = admin.id))
        val section = Sections.create(SectionTmpTest(owner = admin.id, courseId = course.id))
        val assignment = Assignments.create(AssignmentTmpTest(owner = admin.id, courseId = course.id))
        val group = Groups.create(GroupTmpTest(sectionId = section.id, assignmentId = assignment.id))

        val user = fakeUser

        Groups.enrolled(user.id, assignment.id) must beNone
      }
    }

    "return the Group if the user is enrolled in a group for the assignment" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val admin = fakeUser
        val course = Courses.create(CourseTmpTest(owner = admin.id))
        val section = Sections.create(SectionTmpTest(owner = admin.id, courseId = course.id))
        val assignment = Assignments.create(AssignmentTmpTest(owner = admin.id, courseId = course.id))
        val group = Groups.create(GroupTmpTest(sectionId = section.id, assignmentId = assignment.id))

        val user = fakeUser
        Groups.join(user.id, group.id)

        Groups.enrolled(user.id, assignment.id).get must beEqualTo(group)
      }
    }

    "return the correct Group if the user is enrolled in groups for multiple assignments" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val admin = fakeUser
        val course = Courses.create(CourseTmpTest(owner = admin.id))
        val section = Sections.create(SectionTmpTest(owner = admin.id, courseId = course.id))
        val assignment1 = Assignments.create(AssignmentTmpTest(owner = admin.id, courseId = course.id))
        val group1 = Groups.create(GroupTmpTest(sectionId = section.id, assignmentId = assignment1.id))
        val assignment2 = Assignments.create(AssignmentTmpTest(owner = admin.id, courseId = course.id))
        val group2 = Groups.create(GroupTmpTest(sectionId = section.id, assignmentId = assignment2.id))

        val user = fakeUser
        Groups.join(user.id, group2.id)

        Groups.enrolled(user.id, assignment2.id).get must beEqualTo(group2)
      }
    }
  }


}
