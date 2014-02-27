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
import models.id.UserId

// TODO check out http://workwithplay.com/blog/2013/06/19/integration-testing/
@RunWith(classOf[JUnitRunner])
class SectionSpec extends Specification {

	"granting access to a section" should {

		"grant a student view access to the section and the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.fakeUser(UserTmpTest())
				val course = Courses.create(CoursesTmpTest(owner = owner.id))
				val section = Sections.create(SectionTmpTest(owner = owner.id, courseId = course.id))

				val student = DBTest.fakeUser(UserTmpTest())
				Sections.grantAccess(student, section, View) // View indicates student

				Sections.findDetails(section.id)(student, session).get.a must beEqualTo(View)
				course.access(student, session) must beEqualTo(View)
			}
		}

		"grant a teacher edit access to the section and the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.fakeUser(UserTmpTest())
				val course = Courses.create(CoursesTmpTest(owner = owner.id))
				val section = Sections.create(SectionTmpTest(owner = owner.id, courseId = course.id))

				val student = DBTest.fakeUser(UserTmpTest())
				Sections.grantAccess(student, section, Edit) // Edit indicates Teacher

				Sections.findDetails(section.id)(student, session).get.a must beEqualTo(Edit)
				course.access(student, session) must beEqualTo(Edit)
			}
		}

		"never lower access to a course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val courseOwner = DBTest.fakeUser(UserTmpTest())
				val course = Courses.create(CoursesTmpTest(owner = courseOwner.id))
				val sectionOwner = DBTest.fakeUser(UserTmpTest())
				val section = Sections.create(SectionTmpTest(owner = sectionOwner.id, courseId = course.id))

				Sections.grantAccess(courseOwner, section, View)

				Sections.findDetails(section.id)(courseOwner, session).get.a must beEqualTo(Edit)
				course.access(courseOwner, session) must beEqualTo(Own)
			}
		}
	}
}
