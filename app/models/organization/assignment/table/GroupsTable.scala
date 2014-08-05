package models.organization.assignment.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import org.joda.time.DateTime
import com.artclod.slick.JodaUTC._
import models.support._
import models.organization.assignment._
import models.organization.table._

class GroupsTable(tag: Tag) extends Table[Group](tag, "assignment_groups") {
  def id = column[GroupId]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def sectionId = column[SectionId]("section_id", O.NotNull)
  def assignmentId = column[AssignmentId]("assignment_id", O.NotNull)
  def creationDate = column[DateTime]("creation_date", O.NotNull)
  def updateDate = column[DateTime]("update_date", O.NotNull)

  def * = (id, name , sectionId , assignmentId , creationDate , updateDate) <> (Group.tupled, Group.unapply _)

  def sectionFK = foreignKey("assignment_groups_section_fk", sectionId, sectionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def assignmentFK = foreignKey("assignment_groups_assignment_fk", assignmentId, assignmentsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
