package models.organization

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test.FakeApplication
import models.DBTest.inMemH2
import models.DBTest
import models.question.derivative.Quizzes
import models.question.derivative.TestQuiz
import service._
import viewsupport.question.derivative.StudentQuizResult

// TODO check out http://workwithplay.com/blog/2013/06/19/integration-testing/
@RunWith(classOf[JUnitRunner])
class SectionsSpec extends Specification {

	"results" should {

		"return results for all the students associated with the section" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val course = Courses.create(TestCourse(owner = DBTest.newFakeUser(UserTmpTest()).id))
				val section = Sections.create(TestSection(owner = DBTest.newFakeUser(UserTmpTest()).id, courseId = course.id))
				val quiz = Quizzes.create(TestQuiz(owner = DBTest.newFakeUser(UserTmpTest()).id), course.id)
				
				val student1 = DBTest.newFakeUser(UserTmpTest())
				section.grantAccess(View)(student1, session)
				val student2 = DBTest.newFakeUser(UserTmpTest())
				section.grantAccess(View)(student2, session)
				
				val student1Results = StudentQuizResult(student1, quiz, List())
				val student2Results = StudentQuizResult(student2, quiz, List())
				section.results(quiz) must beEqualTo(List(student1Results, student2Results))
			}
		}

	}
	
	"students" should {
		"return all the students associated with the section" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val course = Courses.create(TestCourse(owner = DBTest.newFakeUser(UserTmpTest()).id))
				val section = Sections.create(TestSection(owner = DBTest.newFakeUser(UserTmpTest()).id, courseId = course.id))

				val student1 = DBTest.newFakeUser(UserTmpTest())
				section.grantAccess(View)(student1, session)
				val student2 = DBTest.newFakeUser(UserTmpTest())
				section.grantAccess(View)(student2, session)
				
				section.students must beEqualTo(List(student1, student2))
			}
		}
	}
	
	"SectionAccess" should {
		"be Edit for the owner of the course that the section is in" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val couseOwner = DBTest.newFakeUser(UserTmpTest())
				val course = Courses.create(TestCourse(owner = couseOwner.id))
				val sectionOwner = DBTest.newFakeUser(UserTmpTest())
				val section = Sections.create(TestSection(owner = sectionOwner.id, courseId = course.id))

				section.access(couseOwner, session) must beEqualTo(Edit)
			}
		}
		
		"be Own for the owner of the section" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val couseOwner = DBTest.newFakeUser(UserTmpTest())
				val course = Courses.create(TestCourse(owner = couseOwner.id))
				val sectionOwner = DBTest.newFakeUser(UserTmpTest())
				val section = Sections.create(TestSection(owner = sectionOwner.id, courseId = course.id))

				section.access(sectionOwner, session) must beEqualTo(Own)
			}
		}
	}

	"grantAccess" should {

		"grant a student view access to the section and the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.newFakeUser(UserTmpTest())
				val course = Courses.create(TestCourse(owner = DBTest.newFakeUser(UserTmpTest()).id))
				val section = Sections.create(TestSection(owner = owner.id, courseId = course.id))

				val student = DBTest.newFakeUser(UserTmpTest())
				section.grantAccess(View)(student, session) // View indicates student

				section.access(student, session) must beEqualTo(View)
				course.access(student, session) must beEqualTo(View)
			}
		}

		"grant a teacher edit access to the section and the course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val owner = DBTest.newFakeUser(UserTmpTest())
				val course = Courses.create(TestCourse(owner = owner.id))
				val section = Sections.create(TestSection(owner = owner.id, courseId = course.id))

				val student = DBTest.newFakeUser(UserTmpTest())
				section.grantAccess(Edit)(student, session) // Edit indicates Teacher

				section.access(student, session) must beEqualTo(Edit)
				course.access(student, session) must beEqualTo(Edit)
			}
		}

		"never lower access to a course" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val courseOwner = DBTest.newFakeUser(UserTmpTest())
				val course = Courses.create(TestCourse(owner = courseOwner.id))
				val sectionOwner = DBTest.newFakeUser(UserTmpTest())
				val section = Sections.create(TestSection(owner = sectionOwner.id, courseId = course.id))

				section.grantAccess(View)(courseOwner, session)

				section.access(courseOwner, session) must beEqualTo(Edit)
				course.access(courseOwner, session) must beEqualTo(Own)
			}
		}
		
		"never lower access to a section" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val course = Courses.create(TestCourse(owner = DBTest.newFakeUser(UserTmpTest()).id))
				val section = Sections.create(TestSection(owner = DBTest.newFakeUser(UserTmpTest()).id, courseId = course.id))

				val user = DBTest.newFakeUser(UserTmpTest())
				
				section.grantAccess(Edit)(user, session)
				section.grantAccess(View)(user, session)

				section.access(user, session) must beEqualTo(Edit)
			}
		}
		
		"doesn't grant access to another course's section" in new WithApplication(FakeApplication(additionalConfiguration = inMemH2)) {
			DB.withSession { implicit session: Session =>
				val course1 = Courses.create(TestCourse(owner = DBTest.newFakeUser(UserTmpTest()).id))
				val section1 = Sections.create(TestSection(owner = DBTest.newFakeUser(UserTmpTest()).id, courseId = course1.id))
				val course2 = Courses.create(TestCourse(owner = DBTest.newFakeUser(UserTmpTest()).id))
				val section2 = Sections.create(TestSection(owner = DBTest.newFakeUser(UserTmpTest()).id, courseId = course2.id))

				val user = DBTest.newFakeUser(UserTmpTest())
				
				section1.grantAccess(Edit)(user, session)

				section1.access(user, session) must beEqualTo(Edit)
				section2.access(user, session) must beEqualTo(Non)
			}
		}
	}
}
