package models.organization

import play.api.db.slick.Config.driver.simple._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick.DB
import scala.slick.session.Session
import models.DBTest.inMemH2
import play.api.test.FakeApplication
import models.EquationsModel
import models.DBTest
import models.DBTest.fakeUser
import service.UserTmpTest
import models.organization._
import models.organization.table._
import service._
import service.table._
import models.support.UserId
import models.question.derivative.Quizzes
import models.question.derivative.QuizTmpTest
import viewsupport.question.derivative.UserQuizResults
import models.organization.assignment.Assignments

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
