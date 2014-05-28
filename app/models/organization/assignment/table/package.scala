package models.organization.assignment

import play.api.db.slick.Config.driver.simple._

package object table {

  val assignmentGroupsQuizzesTable = TableQuery[Groups2QuizzesTable]
  val assignmentGroupsTable = TableQuery[GroupsTable]
  val assignmentsTable = TableQuery[AssignmentsTable]
  val usersAssignmentGroupsTable = TableQuery[Users2GroupsTable]

}
