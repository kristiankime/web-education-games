package models.organization.assignment.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import models.support._
import models.question.derivative.table._

case class AssignmentGroup2Quiz(groupId: GroupId, quizId: QuizId)

class AssignmentGroupsQuizzesTable(tag: Tag) extends Table[AssignmentGroup2Quiz](tag, "derivative_assignment_groups_2_quizzes") {
  def groupId = column[GroupId]("group_id", O.NotNull)
  def quizId = column[QuizId]("quiz_id", O.NotNull)
  def * = (groupId, quizId) <> (AssignmentGroup2Quiz.tupled, AssignmentGroup2Quiz.unapply _)

  def pk = primaryKey("derivative_assignment_groups_quizzes_pk", (groupId, quizId))

  def assignmentIdFK = foreignKey("derivative_assignment_groups_quizzes_course_fk", groupId,assignmentGroupsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def questionIdFK = foreignKey("derivative_assignment_groups_quizzes_quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}



