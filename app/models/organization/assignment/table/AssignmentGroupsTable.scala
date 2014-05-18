package models.organization.assignment.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._
import models.support._
import models.organization.assignment._
import models.organization.table._

class AssignmentGroupsTable extends Table[Group]("assignment_groups") {
  def id = column[GroupId]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def sectionId = column[SectionId]("sectionId", O.NotNull)
  def assignmentId = column[AssignmentId]("assignmentId", O.NotNull)
  def creationDate = column[DateTime]("creationDate", O.NotNull)
  def updateDate = column[DateTime]("updateDate", O.NotNull)

  private def columnsNoId = name ~ sectionId ~ assignmentId ~ creationDate ~ updateDate
  def * = id ~: columnsNoId <> (Group, Group.unapply _)

  def sectionFK = foreignKey("assignment_groups_section_fk", sectionId, new SectionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def assignmentFK = foreignKey("assignment_groups_assignment_fk", assignmentId, new AssignmentsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

  private def autoInc = columnsNoId returning id
  def insert(t: GroupTmp)(implicit s: Session) = autoInc.insert(GroupTmp.unapply(t).get)
}
