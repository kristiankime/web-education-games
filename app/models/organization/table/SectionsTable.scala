package models.organization.table

import play.api.db.slick.Config.driver.simple._
import com.artclod.slick.Joda._
import org.joda.time.DateTime
import models.organization._
import models.support._
import service.table.UsersTable
import scala.slick.model.ForeignKeyAction

class SectionsTable(tag: Tag) extends Table[Section](tag, "sections") with IdentifiedAndOwned[Section, SectionId] {
	def id = column[SectionId]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def courseId = column[CourseId]("courseId", O.NotNull)
	def owner = column[UserId]("owner", O.NotNull)
	def editCode = column[String]("editCode", O.NotNull)
	def viewCode = column[String]("viewCode", O.NotNull)
	def creationDate = column[DateTime]("creationDate", O.NotNull)
	def updateDate = column[DateTime]("updateDate", O.NotNull)

	def * = (id, name, courseId, owner, editCode, viewCode, creationDate, updateDate) <> (Section.tupled, Section.unapply _)

	def ownerFK = foreignKey("sections_owner_fk", owner, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def courseFK = foreignKey("sections_courses_fk", courseId, coursesTable)(_.id, onDelete = ForeignKeyAction.Cascade)

//  def autoInc = name ~ courseId ~ owner ~ editCode ~ viewCode ~ creationDate ~ updateDate returning id

//	def insert(t: SectionTmp)(implicit s: Session) = this.autoInc.insert(t.name, t.courseId, t.owner, t.editCode, t.viewCode, t.date, t.date)

}
