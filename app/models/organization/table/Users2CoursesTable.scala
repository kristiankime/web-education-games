package models.organization.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import service.table._
import service._
import models.support._
import models.organization.Courses
import models.support.table.{UserLinkRow, UserLink}

case class User2Course(userId: UserId, courseId: CourseId, access: Access) extends UserLinkRow

class Users2CoursesTable(tag: Tag) extends Table[User2Course](tag, "users_2_courses") with UserLink[User2Course, CourseId] {
	def userId = column[UserId]("user_id", O.NotNull)
	def id = column[CourseId]("course_id", O.NotNull)
	def access = column[Access]("access", O.NotNull)
	def * = (userId, id, access) <> (User2Course.tupled, User2Course.unapply _)

	def pk = primaryKey("users_2_courses_pk", (userId, id))

	def userIdFK = foreignKey("users_2_courses_user_fk", userId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("users_2_courses_course_fk", id, coursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}

object Users2CoursesTable{
  val usersCoursesTable = TableQuery[Users2CoursesTable]

  def raiseAccess(v: User2Course)(implicit s: Session) = {
    val currentAccess = usersCoursesTable.where(r => r.userId === v.userId && r.id === v.courseId).firstOption
    currentAccess match {
      case None => usersCoursesTable += v
      case Some(old) => {
        if (old.access.v < v.access.v) { usersCoursesTable.where(r => r.userId === v.userId && r.id === v.courseId).update(v) }
      }
    }
  }
}