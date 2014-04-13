package models.organization.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.support._

case class User2Course(userId: UserId, courseId: CourseId, access: Access) extends UserLinkRow

class UsersCoursesTable extends Table[User2Course]("users_courses") with UserLink[User2Course, CourseId] {
	def userId = column[UserId]("user_id", O.NotNull)
	def id = column[CourseId]("course_id", O.NotNull)
	def access = column[Access]("access", O.NotNull)
	def * = userId ~ id ~ access <> (User2Course, User2Course.unapply _)

	def pk = primaryKey("users_courses_pk", (userId, id))

	def userIdFK = foreignKey("users_courses_user_fk", userId, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("users_courses_course_fk", id, new CoursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def raiseAccess(v: User2Course)(implicit s: Session) = {
		val currentAccess = Query(this).where(r => r.userId === v.userId && r.id === v.courseId).firstOption
		currentAccess match {
			case None => (this).insert(v)
			case Some(old) => {
				if (old.access.v < v.access.v) { Query(this).where(r => r.userId === v.userId && r.id === v.courseId).update(v) }
			}
		}
	}
}