package models.organization.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.question.derivative._
import models.question.derivative.table.QuizesTable
import models.id.Ids._
import models.id._

case class User2Course(userId: UID, courseId: Long)

object UsersCoursesTable extends Table[User2Course]("users_courses") {
	def userId = column[UID]("user_id", O.NotNull)
	def courseId = column[Long]("course_id", O.NotNull)
	def * = userId ~ courseId <> (User2Course, User2Course.unapply _)

	def pk = primaryKey("users_courses_pk", (userId, courseId))

	def userIdFK = foreignKey("users_courses_user_fk", userId, UserTable)(_.uid, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("users_courses_course_fk", courseId, CoursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}