package models.organization.assignment.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import org.joda.time.DateTime
import com.artclod.slick.Joda._
import models.support._
import models.organization.assignment._
import models.organization.table._

class AssignmentGroupsTable(tag: Tag) extends Table[Group](tag, "assignment_groups") {
  def id = column[GroupId]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def sectionId = column[SectionId]("sectionId", O.NotNull)
  def assignmentId = column[AssignmentId]("assignmentId", O.NotNull)
  def creationDate = column[DateTime]("creationDate", O.NotNull)
  def updateDate = column[DateTime]("updateDate", O.NotNull)

  def * = (id, name , sectionId , assignmentId , creationDate , updateDate) <> (Group.tupled, Group.unapply _)

  def sectionFK = foreignKey("assignment_groups_section_fk", sectionId, sectionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def assignmentFK = foreignKey("assignment_groups_assignment_fk", assignmentId, assignmentsTable)(_.id, onDelete = ForeignKeyAction.Cascade)

//  private def autoInc = columnsNoId returning id
//  def insert(t: GroupTmp)(implicit s: Session) = autoInc.insert(GroupTmp.unapply(t).get)
}
