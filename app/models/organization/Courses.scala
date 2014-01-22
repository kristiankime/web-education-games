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

case class Course(id: CourseId, name: String, creationDate: DateTime, updateDate: DateTime)

case class CourseTmp(name: String, date: DateTime) {
	def apply(id: CourseId) = { Course(id, name, date, date) }
}

object Courses {
	
	def list = DB.withSession { implicit session: Session =>
		(for (
			s <- SectionsTable;
			c <- CoursesTable if s.courseId === c.id
		) yield (c, s)).list.groupBy(_._1).mapValues(_.map(_._2))
	}

	
	def create(teacher: User, courseInfo: CourseTmp) = DB.withSession { implicit session: Session =>
		val courseId = CoursesTable.insert(courseInfo)
		UsersCoursesTable.insert(User2Course(teacher.id, courseId, Own))
		courseId
	}
	
	def find(id: CourseId) = DB.withSession { implicit session: Session =>
		Query(CoursesTable).where(_.id === id).firstOption
	}
}
