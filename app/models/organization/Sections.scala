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

case class SectionTmp(name: String, courseId: CourseId, owner: UserId, editCode: String, viewCode: String, date: DateTime) {
	def apply(id: SectionId) = { Section(id, name, courseId, owner, editCode, viewCode, date, date) }
}

case class Section(id: SectionId, name: String, courseId: CourseId, owner: UserId, editCode: String, viewCode: String, creationDate: DateTime, updateDate: DateTime) extends Secured {

	def course(implicit session: Session) = Courses.find(courseId).get

	def details(implicit user: User, session: Session) = SectionDetails(this, access)

	def otherAccess(implicit user: User, session: Session): Access = Sections.otherAccess(user, id)

	def access(implicit user: User, session: Session) = SectionAccess(this)
	
	def grantAccess(access: Access)(implicit user: User, session: Session) = Sections.grantAccess(this, access)

}

object SectionAccess {
	def apply(section: Section)(implicit user: User, session: Session) = {
		val course = section.course.access.maxEdit
		val direct = section.directAccess
		Seq(course, direct).max
	}
}

object Sections {

	// ======= CREATE ======
	def create(sectionTmp: SectionTmp)(implicit session: Session) = sectionTmp((new SectionsTable).insert(sectionTmp))

	// ======= FIND ======
	def find(sectionId: SectionId)(implicit session: Session) = Query(new SectionsTable).where(_.id === sectionId).firstOption

	def findByCourse(courseId: CourseId)(implicit session: Session) = Query(new SectionsTable).where(_.courseId === courseId).list

	def findDetails(sectionId: SectionId)(implicit user: User, session: Session) = Query(new SectionsTable).where(_.id === sectionId).firstOption.map(_.details)

	// ======= AUTHORIZATION ======
	def otherAccess(user: User, sectionId: SectionId)(implicit session: Session) =
		Query(new UsersSectionsTable).where(us => us.userId === user.id && us.id === sectionId).firstOption.map(_.access).toAccess

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
