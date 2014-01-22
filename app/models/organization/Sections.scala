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

case class Section(id: SectionId, name: String, courseId: CourseId, creationDate: DateTime, updateDate: DateTime)

case class SectionTmp(name: String, courseId: CourseId, date: DateTime) {
	def apply(id: SectionId) = { Section(id, name, courseId, date, date) }
}

object Sections {
	
	def findByCourse(courseId: CourseId) = DB.withSession { implicit session: Session =>
		Query(SectionsTable).where(_.courseId === courseId).list
	}
	
	def create(teacher: User, sectionInfo: SectionTmp) = DB.withSession { implicit session: Session =>
		val sectionId = SectionsTable.insert(sectionInfo)
		UsersSectionsTable.insert(User2Section(teacher.id, sectionId, Own))
		sectionId
	}

}
