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

case class Section(id: SectionId, name: String, courseId: CourseId, owner: UserId, editCode: String, viewCode: String, creationDate: DateTime, updateDate: DateTime) extends Secured[Option[Course]] {
	def otherAccess(course: Option[Course])(implicit user: User, session: Session) : Access = Sections.otherAccess(user, id)	
}

case class SectionTmp(name: String, courseId: CourseId, owner: UserId, editCode: String, viewCode: String, date: DateTime) {
	def apply(id: SectionId) = { Section(id, name, courseId, owner, editCode, viewCode, date, date) }
}

object Sections {

	def otherAccess(user: User, sectionId: SectionId)(implicit session: Session) = 
		Query(new UsersSectionsTable).where(us => us.userId === user.id && us.id === sectionId).firstOption.map(_.access).toAccess

	
	def findDetails(sectionId: SectionId)(implicit user: User, session: Session) = {
		val sectionOwner = Queries.owner(sectionId, new SectionsTable)
		val sectionAccess = Queries.access(user, new UsersSectionsTable, sectionOwner)
		sectionAccess.firstOption.map(Access.accessMap(_)).map(v => SectionDetails(v))
	}

	def find(sectionId: SectionId)(implicit session: Session) = Query(new SectionsTable).where(_.id === sectionId).firstOption

	def findByCourse(courseId: CourseId)(implicit session: Session) = Query(new SectionsTable).where(_.courseId === courseId).list

	def create(sectionTmp: SectionTmp)(implicit session: Session) = sectionTmp((new SectionsTable).insert(sectionTmp))

	/**
	 * Granting access to the section also grants access to the course
	 */
	def grantAccess(student: User, section: Section, access: Access)(implicit session: Session) = {
		(new UsersCoursesTable).insert(User2Course(student.id, section.courseId, access))
		(new UsersSectionsTable).insert(User2Section(student.id, section.id, access))
	}

}
