package models.organization.table

import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import models.question.derivative.table._
import models.support._
import com.artclod.slick.JodaUTC.timestamp2DateTime

case class Course2Quiz(courseId: CourseId, quizId: QuizId, startDate: Option[DateTime] = None, endDate: Option[DateTime] = None)

class Courses2QuizzesTable(tag: Tag) extends Table[Course2Quiz](tag, "courses_2_derivative_quizzes") {
	def courseId = column[CourseId]("course_id")
	def quizId = column[QuizId]("quiz_id")
  def startDate = column[Option[DateTime]]("start_date")
  def endDate = column[Option[DateTime]]("end_date")
	def * = (courseId, quizId, startDate, endDate) <> (Course2Quiz.tupled, Course2Quiz.unapply _)

	def pk = primaryKey("courses_2_derivative_quizzes_pk", (courseId, quizId))

	def courseIdFK = foreignKey("courses_2_derivative_quizzes_course_fk", courseId, coursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("courses_2_derivative_quizzes_quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}