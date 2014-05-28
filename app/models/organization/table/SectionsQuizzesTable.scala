package models.organization.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import models.question.derivative.table._
import models.support._

case class Section2Quiz(sectionId: SectionId, quizId: QuizId)

class SectionsQuizzesTable(tag: Tag) extends Table[Section2Quiz](tag, "section_derivative_quizzes") {
	def courseId = column[SectionId]("course_id", O.NotNull)
	def quizId = column[QuizId]("quiz_id", O.NotNull)
	def * = (courseId, quizId) <> (Section2Quiz.tupled, Section2Quiz.unapply _)

	def pk = primaryKey("section_derivative_quizzes_pk", (courseId, quizId))

	def courseIdFK = foreignKey("section_derivative_quizzes_section_fk", courseId, sectionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("section_derivative_quizzes_quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}