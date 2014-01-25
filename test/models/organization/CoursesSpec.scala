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

	"access" should {

		"be Non when a user has no connection to a course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val owner = DBTest.fakeUser(UserTmpTest())
			val accessor = DBTest.fakeUser(UserTmpTest())
			val course = Courses.create(CoursesTmpTest(owner = owner.id))
			val courseDetails = Courses.list(accessor)(0)

			courseDetails.access must beEqualTo(Non)
		}

		"be Own when a user is the owner of the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val owner = DBTest.fakeUser(UserTmpTest())
			val course = Courses.create(CoursesTmpTest(owner = owner.id))
			val courseDetails = Courses.list(owner)(0)

			courseDetails.access must beEqualTo(Own)
		}

		"be Edit when a user is not the owner of the course but has edit access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val owner = DBTest.fakeUser(UserTmpTest())
			val accessor = DBTest.fakeUser(UserTmpTest())
			val course = Courses.create(CoursesTmpTest(owner = owner.id))
			Courses.access(accessor, course, Edit)
			val courseDetails = Courses.list(accessor)(0)

			courseDetails.access must beEqualTo(Edit)
		}

		"be View when a user is not the owner of the course but has view access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val owner = DBTest.fakeUser(UserTmpTest())
			val accessor = DBTest.fakeUser(UserTmpTest())
			val course = Courses.create(CoursesTmpTest(owner = owner.id))
			Courses.access(accessor, course, Edit)
			val courseDetails = Courses.list(accessor)(0)

			courseDetails.access must beEqualTo(Edit)
		}
		
		"be Own when a user is the owner of the course even if they have other access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			val owner = DBTest.fakeUser(UserTmpTest())
			val course = Courses.create(CoursesTmpTest(owner = owner.id))
			Courses.access(owner, course, Edit)
			val courseDetails = Courses.list(owner)(0)

			courseDetails.access must beEqualTo(Own)
		}
	}

}
