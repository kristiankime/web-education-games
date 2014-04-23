package models.organization.table

import play.api.db.slick.Config.driver.simple._
import models.organization._
import models.support._
import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._
import service.table.UserTable
import scala.slick.lifted.ForeignKeyAction

class AssignmentGroupsTable extends Table[AssignmentGroup]("assignment_groups") with IdentifiedAndOwned[AssignmentGroup, AssignmentGroupId] {
  def id = column[AssignmentGroupId]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def assignmentId = column[AssignmentId]("assignmentId", O.NotNull)
  def owner = column[UserId]("owner", O.NotNull)
  def creationDate = column[DateTime]("creationDate", O.NotNull)
  def updateDate = column[DateTime]("updateDate", O.NotNull)

  def * = id ~ name ~ assignmentId ~ owner ~ creationDate ~ updateDate <> (AssignmentGroup, AssignmentGroup.unapply _)

  def ownerFK = foreignKey("assignment_groups_owner_fk", owner, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def courseFK = foreignKey("assignment_groups_assignment_fk", assignmentId, new AssignmentsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

  def autoInc = name ~ assignmentId ~ owner ~ creationDate ~ updateDate returning id

  def insert(t: AssignmentGroupTmp)(implicit s: Session) = this.autoInc.insert(t.name, t.assignmentId, t.owner, t.date, t.date)

}
