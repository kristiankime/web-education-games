package models.organization.table

import play.api.db.slick.Config.driver.simple._
import com.artclod.slick.JodaUTC._
import org.joda.time.DateTime
import models.organization._
import models.support._
import service.table.UsersTable
import scala.slick.model.ForeignKeyAction
import models.support.table.IdentifiedAndOwned

class SectionsTable(tag: Tag) extends Table[Section](tag, "sections") with IdentifiedAndOwned[Section, SectionId] {
	def id = column[SectionId]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def courseId = column[CourseId]("course_id", O.NotNull)
	def owner = column[UserId]("owner", O.NotNull)
	def editCode = column[String]("edit_code", O.NotNull)
	def viewCode = column[String]("view_code", O.NotNull)
	def creationDate = column[DateTime]("creation_date", O.NotNull)
	def updateDate = column[DateTime]("update_date", O.NotNull)

	def * = (id, name, courseId, owner, editCode, viewCode, creationDate, updateDate) <> (Section.tupled, Section.unapply _)

	def ownerFK = foreignKey("sections_owner_fk", owner, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def courseFK = foreignKey("sections_courses_fk", courseId, coursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
