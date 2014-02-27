package models.organization

import scala.math.Ordering._
import scala.math.Ordering.Implicits._

import org.joda.time.DateTime

import models.id._
import models.organization.table._
import models.organization.view._
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import service._
import service.table.UserTable
import service._

case class CourseTmp(name: String, owner: UserId, editCode: String, viewCode: String, date: DateTime) {
	def apply(id: CourseId) = Course(id, name, owner, editCode, viewCode, date, date)
}

case class Course(id: CourseId, name: String, owner: UserId, editCode: String, viewCode: String, creationDate: DateTime, updateDate: DateTime) extends Secured {

	def otherAccess(implicit user: User, session: Session): Access = Courses.otherAccess(this)

	def access(implicit user: User, session: Session): Access = CourseAccess(this)

}

/**
 * In terms of access level Users can:
 *     1) Own the Course which grants Own access
 *     2) Have direct access granted at either the Edit or View
 * Access to the course determines access to quizzes etc.
 * This means users who are granted access to Sections should also be granted access to the corresponding course.
 */
object CourseAccess {
	def apply(course: Course)(implicit user: User, session: Session) = course.directAccess
}

object Courses {

	def otherAccess(course: Course)(implicit user: User, session: Session) =
		Query(new UsersCoursesTable).where(uc => uc.userId === user.id && uc.id === course.id).firstOption.map(_.access).toAccess

	def findDetails(courseId: CourseId)(implicit user: User, session: Session) = {
		val courseOwner = Queries.owner(courseId, new CoursesTable)
		val courseAccess = Queries.access(user, new UsersCoursesTable, courseOwner)
		courseAccess.firstOption.map(Access.accessMap(_)).map(v => CourseDetails(v, sectionsDetailsFor(v._1.id)))
	}

	def listDetails(implicit user: User, session: Session) = {
		val coursesOwners = Queries.owners(new CoursesTable)
		val coursesAccess = Queries.access(user, new UsersCoursesTable, coursesOwners)
		coursesAccess.list.map(Access.accessMap(_)).map(v => CourseDetails(v, sectionsDetailsFor(v._1.id)))
	}

	private def sectionsDetailsFor(courseId: CourseId)(implicit user: User, session: Session) = 
//	{
//		val sectionsOwners = (for (
//			s <- (new SectionsTable) if s.courseId === courseId;
//			u <- (new UserTable) if u.id === s.owner
//		) yield (s, u))
//		val sectionsAccess = Queries.access(user, new UsersSectionsTable, sectionsOwners)
//		sectionsAccess.list.map(Access.accessMap(_)).map(SectionDetails(_))
		Query(new SectionsTable).where(_.courseId === courseId).list.map(_.details)
//	}

	def create(courseTmp: CourseTmp)(implicit session: Session) = courseTmp((new CoursesTable).insert(courseTmp))

	def grantAccess(user: User, course: Course, access: Access)(implicit session: Session) = (new UsersCoursesTable).insert(User2Course(user.id, course.id, access))

	def find(id: CourseId)(implicit session: Session) = Query(new CoursesTable).where(_.id === id).firstOption

	def findByUser(userId: UserId)(implicit session: Session) = {
		(for (
			uc <- (new UsersCoursesTable) if uc.userId === userId;
			c <- (new CoursesTable) if uc.id === c.id
		) yield c)
			.union(
				(Query(new CoursesTable).where(_.owner === userId))).list
	}
}
