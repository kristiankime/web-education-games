package models.organization.assignment.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.support._
import models.organization.table._
import models.question.derivative.table._

case class Assignment2Quiz(assignmentId: AssignmentId, quizId: QuizId)

class AssignmentsQuizzesTable extends Table[Assignment2Quiz]("derivative_assignments_quizzes") {
  def assignmentId = column[AssignmentId]("course_id", O.NotNull)
  def quizId = column[QuizId]("quiz_id", O.NotNull)
  def * = assignmentId ~ quizId <> (Assignment2Quiz, Assignment2Quiz.unapply _)

  def pk = primaryKey("derivative_assignments_quizzes_pk", (assignmentId, quizId))

  def assignmentIdFK = foreignKey("derivative_assignments_quizzes_course_fk", assignmentId, new AssignmentsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def questionIdFK = foreignKey("derivative_assignments_quizzes_quiz_fk", quizId, new QuizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}



