package models.organization.table

import play.api.db.slick.Config.driver.simple._
import mathml._
import mathml.scalar._
import models._
import models.question.derivative._
import models.organization._
import models.id._
import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._
import service.table.UserTable
import scala.slick.lifted.ForeignKeyAction

class SectionsTable extends Table[Section]("sections") with IdentifiedAndOwned[Section, SectionId] {
	def id = column[SectionId]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def courseId = column[CourseId]("courseId", O.NotNull)
	def owner = column[UserId]("owner", O.NotNull)
	def editCode = column[String]("editCode", O.NotNull)
	def viewCode = column[String]("viewCode", O.NotNull)
	def creationDate = column[DateTime]("creationDate", O.NotNull)
	def updateDate = column[DateTime]("updateDate", O.NotNull)

	def * = id ~ name ~ courseId ~ owner ~ editCode ~ viewCode ~ creationDate ~ updateDate <> (Section, Section.unapply _)

	def ownerFK = foreignKey("sections_owner_fk", owner, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def autoInc = name ~ courseId ~ owner ~ editCode ~ viewCode ~ creationDate ~ updateDate returning id

	def insert(t: SectionTmp)(implicit s: Session) = this.autoInc.insert(t.name, t.courseId, t.owner, t.editCode, t.viewCode, t.date, t.date)

}
