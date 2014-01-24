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

case class Section(id: SectionId, name: String, courseId: CourseId, owner: UserId, editCode: String, viewCode: String, creationDate: DateTime, updateDate: DateTime)

case class SectionTmp(name: String, courseId: CourseId, owner: UserId, editCode: String, viewCode: String, date: DateTime) {
	def apply(id: SectionId) = { Section(id, name, courseId, owner, editCode, viewCode, date, date) }
}

case class SectionDetails(section: Section, owner: User, access: Access)

object SectionDetails {
	def apply(v: (Section, User, Access)) : SectionDetails = SectionDetails(v._1, v._2, v._3)
}

object Sections {

	def find(sectionId: SectionId) = DB.withSession { implicit session: Session =>
		Query(SectionsTable).where(_.id === sectionId).firstOption
	}

	def findByCourse(courseId: CourseId) = DB.withSession { implicit session: Session =>
		Query(SectionsTable).where(_.courseId === courseId).list
	}

	def create(sectionTmp: SectionTmp) = DB.withSession { implicit session: Session =>
		sectionTmp(SectionsTable.insert(sectionTmp))
	}

	def enroll(student: User, section: Section) = DB.withSession { implicit session: Session =>
		UsersSectionsTable.insert(User2Section(student.id, section.id, View))
	}

}
