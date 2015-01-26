package models.organization.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import service.table._
import service._
import models.support._
import models.support.table.{UserLinkRow, UserLink}

case class User2Course(userId: UserId, courseId: CourseId, access: Access, section: Int) extends UserLinkRow

class Users2CoursesTable(tag: Tag) extends Table[User2Course](tag, "users_2_courses") with UserLink[User2Course, CourseId] {
	def userId = column[UserId]("user_id")
	def id = column[CourseId]("course_id")
	def access = column[Access]("access")
  def section = column[Int]("section")
	def * = (userId, id, access, section) <> (User2Course.tupled, User2Course.unapply _)

	def pk = primaryKey("users_2_courses_pk", (userId, id))

	def userIdFK = foreignKey("users_2_courses__user_fk", userId, LoginsTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("users_2_courses__course_fk", id, coursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
