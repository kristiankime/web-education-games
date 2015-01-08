package models.organization.table

import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import models.support._
import com.artclod.slick.JodaUTC.timestamp2DateTime
import models.quiz.table.quizzesTable

case class Course2Quiz(courseId: CourseId, quizId: QuizId, startDate: Option[DateTime] = None, endDate: Option[DateTime] = None)

class Courses2QuizzesTable(tag: Tag) extends Table[Course2Quiz](tag, "courses_2_quizzes") {
	def courseId = column[CourseId]("course_id")
	def quizId = column[QuizId]("quiz_id")
  def startDate = column[Option[DateTime]]("start_date")
  def endDate = column[Option[DateTime]]("end_date")
	def * = (courseId, quizId, startDate, endDate) <> (Course2Quiz.tupled, Course2Quiz.unapply _)

	def pk = primaryKey("courses_2_quizzes__pk", (courseId, quizId))

	def courseIdFK = foreignKey("courses_2_quizzes__course_fk", courseId, coursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("courses_2_quizzes__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}