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
import service.table.UserTable

case class Course(id: CourseId, name: String, owner: UserId, editCode: String, viewCode: String, creationDate: DateTime, updateDate: DateTime)

case class CourseTmp(name: String, owner: UserId, editCode: String, viewCode: String, date: DateTime) {
	def apply(id: CourseId) = Course(id, name, owner, editCode, viewCode, date, date)
}

case class CourseDetails(course: Course, owner: User, access: Access, sections: List[SectionDetails])

object CourseDetails {
	def apply(v: (Course, User, Access), sections: List[SectionDetails]): CourseDetails = CourseDetails(v._1, v._2, v._3, sections)
}

object Courses {

	def list(implicit user: User) = DB.withSession { implicit session: Session =>
		val coursesOwners = (for (
			c <- CoursesTable;
			u <- UserTable if u.id === c.owner
		) yield (c, u))

		val coursesAccess = Queries.access(user, UsersCoursesTable, coursesOwners)

		coursesAccess.list.map(Access.accessMap(_)).map(v => CourseDetails(v, sectionsDetailsFor(v._1.id)))
	}

	def sectionsDetailsFor(courseId: CourseId)(implicit user: User, session: Session) = {
		val sectionsOwners = (for (
			s <- SectionsTable if s.courseId === courseId;
			u <- UserTable if u.id === s.owner
		) yield (s, u))

		val sectionsAccess = Queries.access(user, UsersSectionsTable, sectionsOwners)

		sectionsAccess.list.map(Access.accessMap(_)).map(SectionDetails(_))
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
			c <- CoursesTable if uc.id === c.id
		) yield c)
			.union(
				(Query(CoursesTable).where(_.owner === userId))).list
	}
}
