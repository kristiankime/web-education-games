package models.organization.assignment.table

import play.api.db.slick.Config.driver.simple._
import models.support._
import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._
import service.table.UserTable
import scala.slick.lifted.ForeignKeyAction
import models.organization.assignment._
import models.organization.table._
import com.artclod.slick._

class AssignmentGroupsTable extends Table[AssignmentGroup]("assignment_groups") {
  def id = column[AssignmentGroupId]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def sectionId = column[SectionId]("sectionId", O.NotNull)
  def assignmentId = column[AssignmentId]("assignmentId", O.NotNull)
  def creationDate = column[DateTime]("creationDate", O.NotNull)
  def updateDate = column[DateTime]("updateDate", O.NotNull)

  private def columnsNoId = name ~ sectionId ~ assignmentId ~ creationDate ~ updateDate
  private def columns = id ~: columnsNoId
  def * = columns <> (AssignmentGroup, AssignmentGroup.unapply _)

  def sectionFK = foreignKey("assignment_groups_section_fk", sectionId, new SectionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def assignmentFK = foreignKey("assignment_groups_assignment_fk", assignmentId, new AssignmentsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

  private def autoInc = columnsNoId returning id
  def insert(t: AssignmentGroupTmp)(implicit s: Session) = this.autoInc.insert(AssignmentGroupTmp.unapply(t).get)
}
