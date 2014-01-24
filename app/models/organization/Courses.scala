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

object Courses {

	def list(implicit user: User) = DB.withSession { implicit session: Session =>
		val coursesAndOwners = (for (
			c <- CoursesTable;
			u <- UserTable if c.owner === u.id
		) yield (c, u)).list

		coursesAndOwners.map(co => courseDetails(user, co._1, co._2))
	}

	private def courseDetails(user: User, c: Course, owner: User)(implicit session: Session) = {
		val access = Option2Access(Query(UsersCoursesTable).where(uc => uc.userId === user.id && uc.courseId === c.id).firstOption.map(_.access))

		val sectionsAndUsers = (for (
			s <- SectionsTable if s.courseId === c.id;
			u <- UserTable if s.owner === u.id
		) yield (s, u)).list
		val sectionsDetails = sectionsAndUsers.map(t => sectionDetails(user, t._1, t._2))

		CourseDetails(c, owner, access, sectionsDetails)
	}

	private def sectionDetails(user: User, s: Section, owner: User)(implicit session: Session) = {
		val access = Option2Access(Query(UsersSectionsTable).where(us => us.userId === user.id && us.sectionId === s.id).firstOption.map(_.access))
		SectionDetails(s, owner, access)
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
