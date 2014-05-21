package models.organization.assignment

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import models.DBTest.inMemH2
import models.DBTest.fakeUser
import models.organization._
import service._
import play.api.test.FakeApplication

// TODO check out http://workwithplay.com/blog/2013/06/19/integration-testing/
@RunWith(classOf[JUnitRunner])
class AssignmentsSpec extends Specification {

  "AssignmentAccess" should {
    "be Edit for the owner of the course that the Assignment is in" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val courseOwner = fakeUser
        val course = Courses.create(CourseTmpTest(owner = courseOwner.id))
        val assignmentOwner = fakeUser
        val assignment = Assignments.create(AssignmentTmpTest(owner = assignmentOwner.id, courseId = course.id))

        assignment.access(courseOwner, session) must beEqualTo(Edit)
      }
    }

    "be Own for the owner of the Assignment" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val courseOwner = fakeUser
        val course = Courses.create(CourseTmpTest(owner = courseOwner.id))
        val assignmentOwner = fakeUser
        val assignment = Assignments.create(AssignmentTmpTest(owner = assignmentOwner.id, courseId = course.id))

        assignment.access(assignmentOwner, session) must beEqualTo(Own)
      }
    }
  }

}
