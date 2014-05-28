package models.organization.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import models.question.derivative.table._
import models.support._

case class Course2Quiz(courseId: CourseId, quizId: QuizId)

class Courses2QuizzesTable(tag: Tag) extends Table[Course2Quiz](tag, "courses_2_derivative_quizzes") {
	def courseId = column[CourseId]("course_id", O.NotNull)
	def quizId = column[QuizId]("quiz_id", O.NotNull)
	def * = (courseId, quizId) <> (Course2Quiz.tupled, Course2Quiz.unapply _)

	def pk = primaryKey("courses_2_derivative_quizzes_pk", (courseId, quizId))

	def courseIdFK = foreignKey("courses_2_derivative_quizzes_course_fk", courseId, coursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("courses_2_derivative_quizzes_quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}