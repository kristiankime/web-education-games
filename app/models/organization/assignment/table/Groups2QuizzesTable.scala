package models.organization.assignment.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import models.support._
import models.question.derivative.table._

case class Group2Quiz(groupId: GroupId, quizId: QuizId)

class Groups2QuizzesTable(tag: Tag) extends Table[Group2Quiz](tag, "assignment_groups_2_derivative_quizzes") {
  def groupId = column[GroupId]("group_id", O.NotNull)
  def quizId = column[QuizId]("quiz_id", O.NotNull)
  def * = (groupId, quizId) <> (Group2Quiz.tupled, Group2Quiz.unapply _)

  def pk = primaryKey("assignment_groups_2_derivative_quizzes_pk", (groupId, quizId))

  def assignmentIdFK = foreignKey("assignment_groups_2_derivative_quizzes_course_fk", groupId,assignmentGroupsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def questionIdFK = foreignKey("assignment_groups_2_derivative_quizzes_quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}



