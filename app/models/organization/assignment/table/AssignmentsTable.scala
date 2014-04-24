package models.organization.assignment.table

import play.api.db.slick.Config.driver.simple._
import models.support._
import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._
import service.table.UserTable
import scala.slick.lifted.ForeignKeyAction
import models.organization.assignment.{Assignment, AssignmentTmp}
import models.organization.table.CoursesTable

class AssignmentsTable extends Table[Assignment]("assignments") with IdentifiedAndOwned[Assignment, AssignmentId] {
  def id = column[AssignmentId]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def courseId = column[CourseId]("courseId", O.NotNull)
  def owner = column[UserId]("owner", O.NotNull)
  def creationDate = column[DateTime]("creationDate", O.NotNull)
  def updateDate = column[DateTime]("updateDate", O.NotNull)
  def startDate = column[DateTime]("startDate", O.Nullable)
  def endDate = column[DateTime]("endDate", O.Nullable)

  def * = id ~ name ~ courseId ~ owner ~ creationDate ~ updateDate ~ startDate.? ~ endDate.? <> (Assignment, Assignment.unapply _)

  def ownerFK = foreignKey("assignments_owner_fk", owner, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def courseFK = foreignKey("assignments_courses_fk", courseId, new CoursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)

  def autoInc = name ~ courseId ~ owner ~ creationDate ~ updateDate ~ startDate.? ~ endDate.? returning id

  def insert(t: AssignmentTmp)(implicit s: Session) = this.autoInc.insert(t.name, t.courseId, t.owner, t.date, t.date, t.startDate, t.endDate)

}