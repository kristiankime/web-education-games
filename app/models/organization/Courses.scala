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

case class CourseDetails(c: Course, owner: User, a: Access, sections: List[SectionDetails])

object CourseDetails {
	def apply(v: (Course, User, Access), sections: List[SectionDetails]): CourseDetails = CourseDetails(v._1, v._2, v._3, sections)
}

/**
 * In terms of access level Users can:
 *     1) Own the Course which grants Own access
 *     2) Have direct access granted at either the Edit or View
 * Access to the course determines access to quizzes etc.
 * This means users who are granted access to Sections should also be granted access to the corresponding course.
 */
object Courses {

	def checkAccess(courseId: CourseId)(implicit user: User) = DB.withSession { implicit session: Session =>
		val courseOwner = Queries.owner(courseId, new CoursesTable)
		val courseAccess = Queries.access(user, new UsersCoursesTable, courseOwner)
		courseAccess.firstOption.map(v => Access(user, v._2, v._3))
	}

	def findDetails(courseId: CourseId)(implicit user: User) = DB.withSession { implicit session: Session =>
		val courseOwner = Queries.owner(courseId, new CoursesTable)
		val courseAccess = Queries.access(user, new UsersCoursesTable, courseOwner)
		courseAccess.firstOption.map(Access.accessMap(_)).map(v => CourseDetails(v, sectionsDetailsFor(v._1.id /*, v._3*/ )))
	}

	def listDetails(implicit user: User) = DB.withSession { implicit session: Session =>
		val coursesOwners = Queries.owners(new CoursesTable)
		val coursesAccess = Queries.access(user, new UsersCoursesTable, coursesOwners)
		coursesAccess.list.map(Access.accessMap(_)).map(v => CourseDetails(v, sectionsDetailsFor(v._1.id /*, v._3 */ )))
	}

	private def sectionsDetailsFor(courseId: CourseId /*, access: Access*/ )(implicit user: User, session: Session) = {
		val sectionsOwners = (for (
			s <- (new SectionsTable) if s.courseId === courseId;
			u <- (new UserTable) if u.id === s.owner
		) yield (s, u))
		val sectionsAccess = Queries.access(user, new UsersSectionsTable, sectionsOwners)
		sectionsAccess.list.map(Access.accessMap(_ /*, access*/ )).map(SectionDetails(_))
	}

	def create(courseTmp: CourseTmp) = DB.withSession { implicit session: Session =>
		courseTmp((new CoursesTable).insert(courseTmp))
	}

	def grantAccess(user: User, course: Course, access: Access) = DB.withSession { implicit session: Session =>
		(new UsersCoursesTable).insert(User2Course(user.id, course.id, access))
	}

	def find(id: CourseId) = DB.withSession { implicit session: Session =>
		Query(new CoursesTable).where(_.id === id).firstOption
	}

	def findByUser(userId: UserId) = DB.withSession { implicit session: Session =>
		(for (
			uc <- (new UsersCoursesTable) if uc.userId === userId;
			c <- (new CoursesTable) if uc.id === c.id
		) yield c)
			.union(
				(Query(new CoursesTable).where(_.owner === userId))).list
	}
}
