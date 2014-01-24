package models.organization.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.question.derivative._
import models.question.derivative.table._
import models.id._

case class User2Course(userId: UserId, courseId: CourseId, access: Access)

object UsersCoursesTable extends Table[User2Course]("users_courses") with UserLink[User2Course, CourseId] {
	def userId = column[UserId]("user_id", O.NotNull)
	def id = column[CourseId]("course_id", O.NotNull)
	def access = column[Access]("access", O.NotNull) 
	def * = userId ~ id ~ access <> (User2Course, User2Course.unapply _)

	def pk = primaryKey("users_courses_pk", (userId, id))

	def userIdFK = foreignKey("users_courses_user_fk", userId, UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("users_courses_course_fk", id, CoursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}