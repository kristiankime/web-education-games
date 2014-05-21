package models.organization.assignment

import play.api.db.slick.Config.driver.simple._

package object table {

  val assignmentGroupsQuizzesTable = TableQuery[AssignmentGroupsQuizzesTable]
  val assignmentGroupsTable = TableQuery[AssignmentGroupsTable]
  val assignmentsTable = TableQuery[AssignmentsTable]
  val usersAssignmentGroupsTable = TableQuery[UsersAssignmentGroupsTable]

}
