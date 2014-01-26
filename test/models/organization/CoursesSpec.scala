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
import service.UserTmpTest
import models.organization._
import models.organization.table._
import service._
import service.table._

// TODO check out http://workwithplay.com/blog/2013/06/19/integration-testing/
@RunWith(classOf[JUnitRunner])
class CoursesSpec extends Specification {

	"access to course" should {

		"be Non when a user has no connection to the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val owner = DBTest.fakeUser(UserTmpTest())
			val accessor = DBTest.fakeUser(UserTmpTest())
			val course = Courses.create(CoursesTmpTest(owner = owner.id))
			val courseDetails = Courses.listDetails(accessor)(0)

			courseDetails.a must beEqualTo(Non)
		}

		"be Own when a user is the owner of the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val owner = DBTest.fakeUser(UserTmpTest())
			val course = Courses.create(CoursesTmpTest(owner = owner.id))
			val courseDetails = Courses.listDetails(owner)(0)

			courseDetails.a must beEqualTo(Own)
		}

		"be Edit when a user is not the owner of the course but has edit access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val owner = DBTest.fakeUser(UserTmpTest())
			val accessor = DBTest.fakeUser(UserTmpTest())
			val course = Courses.create(CoursesTmpTest(owner = owner.id))
			Courses.grantAccess(accessor, course, Edit)
			val courseDetails = Courses.listDetails(accessor)(0)

			courseDetails.a must beEqualTo(Edit)
		}

		"be View when a user is not the owner of the course but has view access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val owner = DBTest.fakeUser(UserTmpTest())
			val accessor = DBTest.fakeUser(UserTmpTest())
			val course = Courses.create(CoursesTmpTest(owner = owner.id))
			Courses.grantAccess(accessor, course, Edit)
			val courseDetails = Courses.listDetails(accessor)(0)

			courseDetails.a must beEqualTo(Edit)
		}
		
		"be Own when a user is the owner of the course even if they have other lower access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val owner = DBTest.fakeUser(UserTmpTest())
			val course = Courses.create(CoursesTmpTest(owner = owner.id))
			Courses.grantAccess(owner, course, Edit)
			val courseDetails = Courses.listDetails(owner)(0)

			courseDetails.a must beEqualTo(Own)
		}
	}

	"access to section" should {

		"be Non when a user has no connection to the section" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val owner = DBTest.fakeUser(UserTmpTest())
			val accessor = DBTest.fakeUser(UserTmpTest())
			val course = Courses.create(CoursesTmpTest(owner = owner.id))
			val section = Sections.create(SectionTmpTest(courseId = course.id, owner = owner.id))
			
			val sectionDetails = Courses.findDetails(course.id)(accessor).get.sections(0)

			sectionDetails.a must beEqualTo(Non)
		}
	}
}
