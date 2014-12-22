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
        val organization = Organizations.create(TestOrganization())
        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))
				val user = DBTest.newFakeUser(UserTest())
				
				course.grantAccess(Edit)(user, session)
				
				course.access(user, session) must beEqualTo(Edit)
			}
		}
		
		"keep the higher access if access is granted twice" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
        val organization = Organizations.create(TestOrganization())
        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))
				val user = DBTest.newFakeUser(UserTest())
				
				course.grantAccess(Edit)(user, session)
				course.grantAccess(View)(user, session)
				
				course.access(user, session) must beEqualTo(Edit)
			}
		}
		
		"only grant access to the requested course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
        val organization = Organizations.create(TestOrganization())
        val course1 = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))
				val course2 = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))
				val course3 = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))
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
        val organization = Organizations.create(TestOrganization())
        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))
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
        val organization = Organizations.create(TestOrganization())
        val course = Courses.create(TestCourse(owner = owner.id, organizationId = organization.id))
				val courses0 = Courses.list(session)(0)

				courses0.access(accessor, session) must beEqualTo(Non)
			}
		}

		"be Own when a user is the owner of the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.newFakeUser(UserTest())
        val organization = Organizations.create(TestOrganization())
        val course = Courses.create(TestCourse(owner = owner.id, organizationId = organization.id))
				val courses0 = Courses.list(session)(0)

				courses0.access(owner, session) must beEqualTo(Own)
			}
		}

		"be Edit when a user is not the owner of the course but has edit access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.newFakeUser(UserTest())
				val accessor = DBTest.newFakeUser(UserTest())
        val organization = Organizations.create(TestOrganization())
        val course = Courses.create(TestCourse(owner = owner.id, organizationId = organization.id))
				Courses.grantAccess(course, Edit)(accessor, session)
				val courses0 = Courses.list(session)(0)

				courses0.access(accessor, session) must beEqualTo(Edit)
			}
		}

		"be View when a user is not the owner of the course but has view access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.newFakeUser(UserTest())
				val accessor = DBTest.newFakeUser(UserTest())
        val organization = Organizations.create(TestOrganization())
        val course = Courses.create(TestCourse(owner = owner.id, organizationId = organization.id))
				Courses.grantAccess(course, Edit)(accessor, session)
				val courses0 = Courses.list(session)(0)

				courses0.access(accessor, session) must beEqualTo(Edit)
			}
		}

		"be Own when a user is the owner of the course even if they have other lower access" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.newFakeUser(UserTest())
        val organization = Organizations.create(TestOrganization())
        val course = Courses.create(TestCourse(owner = owner.id, organizationId = organization.id))
				Courses.grantAccess(course, Edit)(owner, session)
				val course0 = Courses.list(session)(0)

				course0.access(owner, session) must beEqualTo(Own)
			}
		}

	}

  "studentsExcept" should {

    "return none if there are no other students in the class" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val organization = Organizations.create(TestOrganization())
        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))

        val requestingStudent = DBTest.newFakeUser(UserTest())
        Courses.grantAccess(course, View)(requestingStudent, session)

        val students = Courses.studentsExcept(course.id, requestingStudent.id)
        students must beEmpty
      }
    }

    "return other student in the class" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val organization = Organizations.create(TestOrganization())
        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))

        val requestingStudent = DBTest.newFakeUser
        val student1 = DBTest.newFakeUser
        val student2 = DBTest.newFakeUser

        Courses.grantAccess(course, View)(requestingStudent, session)
        Courses.grantAccess(course, View)(student1, session)

        val students = Courses.studentsExcept(course.id, requestingStudent.id).toSet
        students must beEqualTo(Set(student1))
      }
    }

    "return other students in the class" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val organization = Organizations.create(TestOrganization())
        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))

        val requestingStudent = DBTest.newFakeUser
        val student1 = DBTest.newFakeUser
        val student2 = DBTest.newFakeUser

        Courses.grantAccess(course, View)(requestingStudent, session)
        Courses.grantAccess(course, View)(student1, session)
        Courses.grantAccess(course, View)(student2, session)

        val students = Courses.studentsExcept(course.id, requestingStudent.id).toSet
        students must beEqualTo(Set(student1, student2))
      }
    }

    "return only students in the class" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
      DB.withSession { implicit session: Session =>
        val organization = Organizations.create(TestOrganization())
        val course = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))
        val requestingStudent = DBTest.newFakeUser
        val student1 = DBTest.newFakeUser
        val student2 = DBTest.newFakeUser
        Courses.grantAccess(course, View)(requestingStudent, session)
        Courses.grantAccess(course, View)(student1, session)
        Courses.grantAccess(course, View)(student2, session)

        val otherCourse = Courses.create(TestCourse(owner = DBTest.newFakeUser.id, organizationId = organization.id))
        val otherStudent = DBTest.newFakeUser
        Courses.grantAccess(otherCourse, View)(otherStudent, session)

        val students = Courses.studentsExcept(course.id, requestingStudent.id).toSet
        students must beEqualTo(Set(student1, student2))
      }
    }
  }
}
