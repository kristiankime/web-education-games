package models.organization

import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import models.support._
import models.organization.table._
import models.question.derivative._
import models.organization.assignment.Assignments
import viewsupport.organization._
import service._

case class Course(id: CourseId, name: String, ownerId: UserId, editCode: String, viewCode: String, creationDate: DateTime, updateDate: DateTime) extends Secured with HasId[CourseId] {

	def sections(implicit session: Session) = Sections(id)

  def assignments(implicit session: Session) = Assignments(id)

	def results(quiz: Quiz)(implicit session: Session) = sections.map(_.results(quiz))
	
	def sectionResults(quiz: Quiz)(implicit session: Session) = sections.map(s => SectionResults(s, s.results(quiz)))

  def quizzes(implicit session: Session) = Quizzes(id)

	protected def linkAccess(implicit user: User, session: Session): Access = Courses.otherAccess(this)

	/**
	 * In terms of access level Users can:
	 *     1) Own the Course which grants Own access
	 *     2) Have association access granted at either the Edit or View
	 * Access to the course determines access to quizzes etc.
	 * This means users who are granted access to Sections should also be granted access to the corresponding course.
	 */
	def access(implicit user: User, session: Session): Access = directAccess

	def grantAccess(access: Access)(implicit user: User, session: Session) = Courses.grantAccess(this, access)
}

object Courses {
	// ======= CREATE ======
	def create(course: Course)(implicit session: Session) = {
    val courseId = (coursesTable returning coursesTable.map(_.id)) += course
    course.copy(id = courseId)
  }

	// ======= FIND ======
	def apply(id: CourseId)(implicit session: Session) = coursesTable.where(_.id === id).firstOption

	def apply(userId: UserId)(implicit session: Session) = {
		(for (
			uc <- Users2CoursesTable.usersCoursesTable if uc.userId === userId;
			c <- coursesTable if uc.id === c.id
		) yield c)
			.union(
				coursesTable.where(_.owner === userId)).list
	}

  def list(implicit session: Session) = coursesTable.list

	// ======= AUTHORIZATION ======
	def otherAccess(course: Course)(implicit user: User, session: Session) =
    Users2CoursesTable.usersCoursesTable.where(uc => uc.userId === user.id && uc.id === course.id).firstOption.map(_.access).toAccess

	def grantAccess(course: Course, access: Access)(implicit user: User, session: Session) {
		if (course.access < access) {
      Users2CoursesTable.usersCoursesTable.where(uc => uc.userId === user.id && uc.id === course.id).firstOption match {
				case Some(u2c) if u2c.access < access => Users2CoursesTable.usersCoursesTable.where(_.id === course.id).update(User2Course(user.id, course.id, access))
				case None => Users2CoursesTable.usersCoursesTable.insert(User2Course(user.id, course.id, access))
				case _ => {}
			}
		}
	}

}
