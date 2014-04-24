package models.organization.assignment.table

import play.api.db.slick.Config.driver.simple._
import models.support._
import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._
import service.table.UserTable
import scala.slick.lifted.ForeignKeyAction
import models.organization.assignment.{AssignmentGroup, AssignmentGroupTmp}

class AssignmentGroupsTable extends Table[AssignmentGroup]("assignment_groups") {
  def id = column[AssignmentGroupId]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def assignmentId = column[AssignmentId]("assignmentId", O.NotNull)
  def creationDate = column[DateTime]("creationDate", O.NotNull)
  def updateDate = column[DateTime]("updateDate", O.NotNull)

  def * = id ~ name ~ assignmentId ~ creationDate ~ updateDate <> (AssignmentGroup, AssignmentGroup.unapply _)

  def assignmentFK = foreignKey("assignment_groups_assignment_fk", assignmentId, new AssignmentsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

  def autoInc = name ~ assignmentId ~ creationDate ~ updateDate returning id

  def insert(t: AssignmentGroupTmp)(implicit s: Session) = this.autoInc.insert(t.name, t.assignmentId, t.date, t.date)

}
