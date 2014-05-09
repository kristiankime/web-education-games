package models.organization

import play.api.db.slick.Config.driver.simple._
import models.organization.table._
import service._
import models.support._
import org.joda.time.DateTime
import viewsupport.organization._
import service.table._
import models.question.derivative._
import models.organization.assignment._

case class SectionTmp(name: String, courseId: CourseId, owner: UserId, editCode: String, viewCode: String, date: DateTime) {
	def apply(id: SectionId) = { Section(id, name, courseId, owner, editCode, viewCode, date, date) }
}

case class Section(id: SectionId, name: String, courseId: CourseId, owner: UserId, editCode: String, viewCode: String, creationDate: DateTime, updateDate: DateTime) extends Secured {

	def course(implicit session: Session) = Courses(courseId).get

  def quizzes(implicit session: Session) = Quizzes.findByCourse(courseId)

  def assignments(implicit session: Session) = Assignments.find(courseId)

  def groups(assignmentId: AssignmentId)(implicit session: Session) = Groups.find(id, assignmentId)

  def groupDetails(assignmentId: AssignmentId)(implicit session: Session) = SectionGroupDetails(this, groups(assignmentId))

	def details(implicit user: User, session: Session) = SectionDetail(this, this.course, access)

	def students(implicit session: Session) = Sections.students(id)

	def results(quiz: Quiz)(implicit session: Session) = students.map(s => quiz.results(s))

	protected def linkAccess(implicit user: User, session: Session): Access = Sections.linkAccess(user, id)

	/**
	 * In terms of access level Users can:
	 *     1) Own the Section which grants Own access
	 *     2) Have association access granted at either the Edit or View
	 *     3) Have access to the Section's Course which provides a maximum of Edit access to the section
	 * This means users who are granted access to Sections should also be granted access to the corresponding course.
	 */
	def access(implicit user: User, session: Session) = Seq(course.access.ceilEdit, directAccess).max

	def grantAccess(access: Access)(implicit user: User, session: Session) = Sections.grantAccess(this, access)

}

object Sections {

	// ======= CREATE ======
	def create(sectionTmp: SectionTmp)(implicit session: Session) = sectionTmp((new SectionsTable).insert(sectionTmp))

	// ======= FIND ======
	def find(sectionId: SectionId)(implicit session: Session) = Query(new SectionsTable).where(_.id === sectionId).firstOption

	def find(courseId: CourseId)(implicit session: Session) = Query(new SectionsTable).where(_.courseId === courseId).sortBy(_.name).list

  def findDetails(sectionId: SectionId, courseId: CourseId)(implicit user: User, session: Session) = {
    val sectionDetails = Query(new SectionsTable).where(_.id === sectionId).firstOption.map(_.details)
    sectionDetails.flatMap(d => if(d.course.id == courseId) {Some(d)} else {None})
  }

  def students(sectionId: SectionId)(implicit session: Session) =
		(for (
			u <- (new UserTable);
			us <- (new UsersSectionsTable) if us.userId === u.id && us.id === sectionId && us.access === service.Access.view
		) yield u).sortBy(_.lastName).list

	// ======= AUTHORIZATION ======
	def linkAccess(user: User, sectionId: SectionId)(implicit session: Session) =
		(Query(new UsersSectionsTable).where(us => us.userId === user.id && us.id === sectionId).firstOption.map(_.access)).toAccess

	/**
	 * Granting access to the section also grants access to the course
	 */
	def grantAccess(section: Section, access: Access)(implicit user: User, session: Session) {
		if (section.access < access) {
			Query(new UsersSectionsTable).where(r => r.userId === user.id && r.id === section.id).firstOption match {
				case Some(u2s) if u2s.access < access => Query(new UsersSectionsTable).where(_.id === section.id).update(User2Section(user.id, section.id, access))
				case None => (new UsersSectionsTable).insert(User2Section(user.id, section.id, access))
				case _ => {}
			}
		}
		Courses.grantAccess(section.course, access.ceilEdit)
	}

}
