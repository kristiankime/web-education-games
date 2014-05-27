package models.organization.assignment.table

import play.api.db.slick.Config.driver.simple._
import models.support._
import org.joda.time.DateTime
import com.artclod.slick.Joda._
import service.table.UsersTable
import scala.slick.model.ForeignKeyAction
import models.organization.assignment.Assignment
import models.organization.table._
import models.support.table.IdentifiedAndOwned


class AssignmentsTable(tag: Tag) extends Table[Assignment](tag, "assignments") with IdentifiedAndOwned[Assignment, AssignmentId] {
  def id = column[AssignmentId]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def courseId = column[CourseId]("courseId", O.NotNull)
  def owner = column[UserId]("owner", O.NotNull)
  def creationDate = column[DateTime]("creationDate", O.NotNull)
  def updateDate = column[DateTime]("updateDate", O.NotNull)
  def startDate = column[DateTime]("startDate", O.Nullable)
  def endDate = column[DateTime]("endDate", O.Nullable)

  def * = (id, name , courseId , owner , creationDate , updateDate , startDate.? , endDate.?) <> (Assignment.tupled, Assignment.unapply _)

  def ownerFK = foreignKey("assignments_owner_fk", owner, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def courseFK = foreignKey("assignments_courses_fk", courseId, coursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}