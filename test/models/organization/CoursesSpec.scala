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

	"grantAccess" should {
		
		"grant the requested access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val course = Courses.create(CourseTmpTest(owner = DBTest.fakeUser(UserTmpTest()).id))
				val user = DBTest.fakeUser(UserTmpTest())
				
				course.grantAccess(Edit)(user, session)
				
				course.access(user, session) must beEqualTo(Edit)
			}
		}
		
		"keep the higher access if access is granted twice" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val course = Courses.create(CourseTmpTest(owner = DBTest.fakeUser(UserTmpTest()).id))
				val user = DBTest.fakeUser(UserTmpTest())
				
				course.grantAccess(Edit)(user, session)
				course.grantAccess(View)(user, session)
				
				course.access(user, session) must beEqualTo(Edit)
			}
		}
		
		"only grant access to the requested course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val course1 = Courses.create(CourseTmpTest(owner = DBTest.fakeUser(UserTmpTest()).id))
				val course2 = Courses.create(CourseTmpTest(owner = DBTest.fakeUser(UserTmpTest()).id))
				val course3 = Courses.create(CourseTmpTest(owner = DBTest.fakeUser(UserTmpTest()).id))
				val user = DBTest.fakeUser(UserTmpTest())
				
				course1.grantAccess(Edit)(user, session)
				course2.grantAccess(View)(user, session)
				
				course1.access(user, session) must beEqualTo(Edit)
				course2.access(user, session) must beEqualTo(View)
				course3.access(user, session) must beEqualTo(Non)
			}
		}
		
		"only grant the spcified user access to the requested course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val course = Courses.create(CourseTmpTest(owner = DBTest.fakeUser(UserTmpTest()).id))
				val userWithAccess = DBTest.fakeUser(UserTmpTest())
				val userNoAccess = DBTest.fakeUser(UserTmpTest())
				
				course.grantAccess(Edit)(userWithAccess, session)
				
				course.access(userWithAccess, session) must beEqualTo(Edit)
				course.access(userNoAccess, session) must beEqualTo(Non)
			}
		}
	}
	
	"listDetails: access to course" should {

		"be Non when a user has no connection to the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.fakeUser(UserTmpTest())
				val accessor = DBTest.fakeUser(UserTmpTest())
				val course = Courses.create(CourseTmpTest(owner = owner.id))
				val courseDetails = Courses.listDetails(accessor, session)(0)

				courseDetails.a must beEqualTo(Non)
			}
		}

		"be Own when a user is the owner of the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.fakeUser(UserTmpTest())
				val course = Courses.create(CourseTmpTest(owner = owner.id))
				val courseDetails = Courses.listDetails(owner, session)(0)

				courseDetails.a must beEqualTo(Own)
			}
		}

		"be Edit when a user is not the owner of the course but has edit access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.fakeUser(UserTmpTest())
				val accessor = DBTest.fakeUser(UserTmpTest())
				val course = Courses.create(CourseTmpTest(owner = owner.id))
				Courses.grantAccess(course, Edit)(accessor, session)
				val courseDetails = Courses.listDetails(accessor, session)(0)

				courseDetails.a must beEqualTo(Edit)
			}
		}

		"be View when a user is not the owner of the course but has view access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.fakeUser(UserTmpTest())
				val accessor = DBTest.fakeUser(UserTmpTest())
				val course = Courses.create(CourseTmpTest(owner = owner.id))
				Courses.grantAccess(course, Edit)(accessor, session)
				val courseDetails = Courses.listDetails(accessor, session)(0)

				courseDetails.a must beEqualTo(Edit)
			}
		}

		"be Own when a user is the owner of the course even if they have other lower access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.fakeUser(UserTmpTest())
				val course = Courses.create(CourseTmpTest(owner = owner.id))
				Courses.grantAccess(course, Edit)(owner, session)
				val courseDetails = Courses.listDetails(owner, session)(0)

				courseDetails.a must beEqualTo(Own)
			}
		}

		"be Own for the owner, Edit for a section teacher and View for a section student" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.fakeUser(UserTmpTest())
				val course = Courses.create(CourseTmpTest(owner = owner.id))
				val section = Sections.create(SectionTmpTest(owner = owner.id, courseId = course.id))
				
				val teacher = DBTest.fakeUser(UserTmpTest())
				Sections.grantAccess(section, Edit)(teacher, session)

				val student = DBTest.fakeUser(UserTmpTest())
				Sections.grantAccess(section, View)(student, session)
				
				Courses.listDetails(owner, session)(0).a must beEqualTo(Own)
				Courses.listDetails(teacher, session)(0).a must beEqualTo(Edit)
				Courses.listDetails(student, session)(0).a must beEqualTo(View)
			}
		}
	}

	"access to section" should {

		"be Non when a user has no connection to the section" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.fakeUser(UserTmpTest())
				val accessor = DBTest.fakeUser(UserTmpTest())
				val course = Courses.create(CourseTmpTest(owner = owner.id))
				val section = Sections.create(SectionTmpTest(courseId = course.id, owner = owner.id))

				val sectionDetails = Courses.findDetails(course.id)(accessor, session).get.sections(0)

				sectionDetails.a must beEqualTo(Non)
			}
		}
	}
}
