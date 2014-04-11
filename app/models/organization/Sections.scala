package models.organization

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import mathml._
import mathml.scalar._
import models.question.derivative.table._
import models.organization.table._
import service._
import models.id._
import org.joda.time.DateTime
import models.organization.view._
import service.table._
import service.Access._
import models.question.derivative.Quiz

case class SectionTmp(name: String, courseId: CourseId, owner: UserId, editCode: String, viewCode: String, date: DateTime) {
	def apply(id: SectionId) = { Section(id, name, courseId, owner, editCode, viewCode, date, date) }
}

case class Section(id: SectionId, name: String, courseId: CourseId, owner: UserId, editCode: String, viewCode: String, creationDate: DateTime, updateDate: DateTime) extends Secured {

	def course(implicit session: Session) = Courses.find(courseId).get

	def details(implicit user: User, session: Session) = SectionDetails(this, this.course, access)

	def students(implicit session: Session) = Sections.students(id)

	def results(quiz: Quiz)(implicit session: Session) = students.map(s => quiz.results(s))

	protected def linkAccess(implicit user: User, session: Session): Access = Sections.linkAccess(user, id)

	/**
	 * In terms of access level Users can:
	 *     1) Own the Section which grants Own access
	 *     2) Have association access granted at either the Edit or View
	 *     2) Have access to the Section's Course which provides a maximum of Edit access to the section
	 * This means users who are granted access to Sections should also be granted access to the corresponding course.
	 */
	def access(implicit user: User, session: Session) = {
		val courseAccess = course.access.maxEdit
		Seq(courseAccess, directAccess).max
	}

	def grantAccess(access: Access)(implicit user: User, session: Session) = Sections.grantAccess(this, access)

}

object Sections {

	// ======= CREATE ======
	def create(sectionTmp: SectionTmp)(implicit session: Session) = sectionTmp((new SectionsTable).insert(sectionTmp))

	// ======= FIND ======
	def find(sectionId: SectionId)(implicit session: Session) = Query(new SectionsTable).where(_.id === sectionId).firstOption

	def findByCourse(courseId: CourseId)(implicit session: Session) = Query(new SectionsTable).where(_.courseId === courseId).list

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
		Courses.grantAccess(section.course, access.maxEdit)
	}

}
