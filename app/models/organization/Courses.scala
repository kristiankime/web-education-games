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

case class Course(id: CourseId, name: String, owner: UserId, editCode: String, viewCode: String, creationDate: DateTime, updateDate: DateTime)

case class CourseTmp(name: String, owner: UserId, editCode: String, viewCode: String, date: DateTime) {
	def apply(id: CourseId) = Course(id, name, owner, editCode, viewCode, date, date)
}

object Courses {

	def list = DB.withSession { implicit session: Session =>
		// LATER do this more efficiently in one call 
		val courses = Query(CoursesTable).list
		val coursesAndSections = courses.map(c => (c, Query(SectionsTable).where(_.courseId === c.id).list))
		coursesAndSections
	}

	def create(courseTmp: CourseTmp) = DB.withSession { implicit session: Session =>
		courseTmp(CoursesTable.insert(courseTmp))
	}

	def find(id: CourseId) = DB.withSession { implicit session: Session =>
		Query(CoursesTable).where(_.id === id).firstOption
	}

	def findByUser(userId: UserId) = DB.withSession { implicit session: Session =>
		(for (
			uc <- UsersCoursesTable if uc.userId === userId;
			c <- CoursesTable if uc.courseId === c.id
		) yield c)
			.union(
				(Query(CoursesTable).where(_.owner === userId))).list
	}
}
