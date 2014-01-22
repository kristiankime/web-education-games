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

//	def coursesAndEnrollment(courseId: CourseId)(implicit user: User) = DB.withSession { implicit session: Session =>
//		val course = Query(CoursesTable).where(_.id === courseId).firstOption
//		val enrolled = Query(UsersCoursesTable).where(uc => uc.userId === user.id && uc.courseId === courseId).firstOption.nonEmpty
//		val quizes = (for {
//			cq <- CoursesQuizzesTable if cq.courseId === courseId
//			q <- QuizzesTable if cq.quizId === q.id
//		} yield q).list
//
//		course.map((_, enrolled, quizes))
//	}
//
//	def coursesAndEnrollment(implicit user: User) = DB.withSession { implicit session: Session =>
//		(for {
//			c <- CoursesTable
//			uc <- UsersCoursesTable if c.id === uc.courseId && uc.userId === user.id
//		} yield (c, uc != null)).list
//
//	}
//
//	def createCourse(teacher: User, courseInfo: CourseTmp, quizes: QuizId*) = DB.withSession { implicit session: Session =>
//		val courseId = CoursesTable.insert(courseInfo)
//		UsersCoursesTable.insert(User2Course(teacher.id, courseId, Own))
//		CoursesQuizzesTable.insertAll(quizes.map(Course2Quiz(courseId, _)): _*)
//		courseId
//	}
	
	def list = DB.withSession { implicit session: Session =>
		Query(CoursesTable).list
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
