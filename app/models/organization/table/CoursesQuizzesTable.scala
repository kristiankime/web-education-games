package models.organization.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.question.derivative._
import models.question.derivative.table.QuizzesTable
import models.support._

case class Course2Quiz(courseId: CourseId, quizId: QuizId)

class CoursesQuizzesTable extends Table[Course2Quiz]("derivative_courses_quizzes") {
	def courseId = column[CourseId]("course_id", O.NotNull)
	def quizId = column[QuizId]("quiz_id", O.NotNull)
	def * = courseId ~ quizId <> (Course2Quiz, Course2Quiz.unapply _)

	def pk = primaryKey("derivative_courses_quizzes_pk", (courseId, quizId))

	def courseIdFK = foreignKey("derivative_courses_quizzes_course_fk", courseId, new CoursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("derivative_courses_quizzes_quiz_fk", quizId, new QuizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}