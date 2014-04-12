package models.organization.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.question.derivative._
import models.question.derivative.table.QuizzesTable
import models.support._

case class Section2Quiz(sectionId: SectionId, quizId: QuizId)

class SectionsQuizzesTable extends Table[Section2Quiz]("derivative_section_quizzes") {
	def courseId = column[SectionId]("course_id", O.NotNull)
	def quizId = column[QuizId]("quiz_id", O.NotNull)
	def * = courseId ~ quizId <> (Section2Quiz, Section2Quiz.unapply _)

	def pk = primaryKey("derivative_section_quizzes_pk", (courseId, quizId))

	def courseIdFK = foreignKey("derivative_section_quizzes_section_fk", courseId, new SectionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("derivative_section_quizzes_quiz_fk", quizId, new QuizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}