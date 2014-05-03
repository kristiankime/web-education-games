package models.organization

import org.joda.time.DateTime
import models.support._
import models.organization.table._
import viewsupport.organization._
import play.api.db.slick.Config.driver.simple._
import service._
import models.question.derivative.{Quizzes, Quiz}
import models.organization.assignment.Assignments

case class CourseTmp(name: String, owner: UserId, editCode: String, viewCode: String, date: DateTime) {
	def apply(id: CourseId) = Course(id, name, owner, editCode, viewCode, date, date)
}

case class Course(id: CourseId, name: String, owner: UserId, editCode: String, viewCode: String, creationDate: DateTime, updateDate: DateTime) extends Secured {

	def sections(implicit session: Session) = Sections.find(id)

  def assignments(implicit session: Session) = Assignments.find(id)

	def results(quiz: Quiz)(implicit session: Session) = sections.map(_.results(quiz))
	
	def sectionResults(quiz: Quiz)(implicit session: Session) = sections.map(s => SectionResults(s, s.results(quiz)))
	
	def details(implicit user: User, session: Session) = CourseDetail(this, access, Sections.find(id).map(_.details))

  def quizzes(implicit session: Session) = Quizzes.findByCourse(id)

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
	def create(courseTmp: CourseTmp)(implicit session: Session) = courseTmp((new CoursesTable).insert(courseTmp))

	// ======= FIND ======
	def find(id: CourseId)(implicit session: Session) = Query(new CoursesTable).where(_.id === id).firstOption

	def find(userId: UserId)(implicit session: Session) = {
		(for (
			uc <- (new UsersCoursesTable) if uc.userId === userId;
			c <- (new CoursesTable) if uc.id === c.id
		) yield c)
			.union(
				(Query(new CoursesTable).where(_.owner === userId))).list
	}

  def list(implicit user: User, session: Session) = Query(new CoursesTable).list

  def listDetails(implicit user: User, session: Session) = list.map(_.details)

	// ======= AUTHORIZATION ======
	def otherAccess(course: Course)(implicit user: User, session: Session) =
		Query(new UsersCoursesTable).where(uc => uc.userId === user.id && uc.id === course.id).firstOption.map(_.access).toAccess

	def grantAccess(course: Course, access: Access)(implicit user: User, session: Session) {
		if (course.access < access) {
			Query(new UsersCoursesTable).where(r => r.userId === user.id && r.id === course.id).firstOption match {
				case Some(u2c) if u2c.access < access => Query(new UsersCoursesTable).where(_.id === course.id).update(User2Course(user.id, course.id, access))
				case None => (new UsersCoursesTable).insert(User2Course(user.id, course.id, access))
				case _ => {}
			}
		}
	}

}
