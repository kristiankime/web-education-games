package models.organization

import play.api.db.slick.Config.driver.simple._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick.DB
import models.DBTest.inMemH2
import play.api.test.FakeApplication
import models.Equations
import models.DBTest
import service.UserTest
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
				val course = Courses.create(TestCourse(owner = DBTest.newFakeUser(UserTest()).id))
				val user = DBTest.newFakeUser(UserTest())
				
				course.grantAccess(Edit)(user, session)
				
				course.access(user, session) must beEqualTo(Edit)
			}
		}
		
		"keep the higher access if access is granted twice" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val course = Courses.create(TestCourse(owner = DBTest.newFakeUser(UserTest()).id))
				val user = DBTest.newFakeUser(UserTest())
				
				course.grantAccess(Edit)(user, session)
				course.grantAccess(View)(user, session)
				
				course.access(user, session) must beEqualTo(Edit)
			}
		}
		
		"only grant access to the requested course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val course1 = Courses.create(TestCourse(owner = DBTest.newFakeUser(UserTest()).id))
				val course2 = Courses.create(TestCourse(owner = DBTest.newFakeUser(UserTest()).id))
				val course3 = Courses.create(TestCourse(owner = DBTest.newFakeUser(UserTest()).id))
				val user = DBTest.newFakeUser(UserTest())
				
				course1.grantAccess(Edit)(user, session)
				course2.grantAccess(View)(user, session)
				
				course1.access(user, session) must beEqualTo(Edit)
				course2.access(user, session) must beEqualTo(View)
				course3.access(user, session) must beEqualTo(Non)
			}
		}
		
		"only grant the specified user access to the requested course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val course = Courses.create(TestCourse(owner = DBTest.newFakeUser(UserTest()).id))
				val userWithAccess = DBTest.newFakeUser(UserTest())
				val userNoAccess = DBTest.newFakeUser(UserTest())
				
				course.grantAccess(Edit)(userWithAccess, session)
				
				course.access(userWithAccess, session) must beEqualTo(Edit)
				course.access(userNoAccess, session) must beEqualTo(Non)
			}
		}
	}
	
	"list: access to course" should {

		"be Non when a user has no connection to the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.newFakeUser(UserTest())
				val accessor = DBTest.newFakeUser(UserTest())
				val course = Courses.create(TestCourse(owner = owner.id))
				val courses0 = Courses.list(session)(0)

				courses0.access(accessor, session) must beEqualTo(Non)
			}
		}

		"be Own when a user is the owner of the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.newFakeUser(UserTest())
				val course = Courses.create(TestCourse(owner = owner.id))
				val courses0 = Courses.list(session)(0)

				courses0.access(owner, session) must beEqualTo(Own)
			}
		}

		"be Edit when a user is not the owner of the course but has edit access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.newFakeUser(UserTest())
				val accessor = DBTest.newFakeUser(UserTest())
				val course = Courses.create(TestCourse(owner = owner.id))
				Courses.grantAccess(course, Edit)(accessor, session)
				val courses0 = Courses.list(session)(0)

				courses0.access(accessor, session) must beEqualTo(Edit)
			}
		}

		"be View when a user is not the owner of the course but has view access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.newFakeUser(UserTest())
				val accessor = DBTest.newFakeUser(UserTest())
				val course = Courses.create(TestCourse(owner = owner.id))
				Courses.grantAccess(course, Edit)(accessor, session)
				val courses0 = Courses.list(session)(0)

				courses0.access(accessor, session) must beEqualTo(Edit)
			}
		}

		"be Own when a user is the owner of the course even if they have other lower access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.newFakeUser(UserTest())
				val course = Courses.create(TestCourse(owner = owner.id))
				Courses.grantAccess(course, Edit)(owner, session)
				val course0 = Courses.list(session)(0)

				course0.access(owner, session) must beEqualTo(Own)
			}
		}

		"be Own for the owner, Edit for a section teacher and View for a section student" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.newFakeUser(UserTest())
				val course = Courses.create(TestCourse(owner = owner.id))
				val section = Sections.create(TestSection(owner = owner.id, courseId = course.id))
				
				val teacher = DBTest.newFakeUser(UserTest())
				Sections.grantAccess(section, Edit)(teacher, session)

				val student = DBTest.newFakeUser(UserTest())
				Sections.grantAccess(section, View)(student, session)
				
				Courses.list(session)(0).access(owner, session) must beEqualTo(Own)
				Courses.list(session)(0).access(teacher, session) must beEqualTo(Edit)
				Courses.list(session)(0).access(student, session) must beEqualTo(View)
			}
		}
	}

	"access to section" should {

		"be Non when a user has no connection to the section" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.newFakeUser(UserTest())
				val accessor = DBTest.newFakeUser(UserTest())
				val course = Courses.create(TestCourse(owner = owner.id))
				val section = Sections.create(TestSection(courseId = course.id, owner = owner.id))

				val section0 = Courses(course.id)(session).get.sections(session)(0)

				section0.access(accessor, session) must beEqualTo(Non)
			}
		}
	}
}
