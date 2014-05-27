package models.organization.table

import com.artclod.slick.Joda._
import play.api.db.slick.Config.driver.simple._
import models.organization._
import models.support._
import org.joda.time.DateTime
import service.table.UsersTable
import scala.slick.model.ForeignKeyAction
import models.support.table.IdentifiedAndOwned

class CoursesTable(tag: Tag) extends Table[Course](tag, "courses") with IdentifiedAndOwned[Course, CourseId] {
	def id = column[CourseId]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def owner = column[UserId]("owner", O.NotNull)
	def editCode = column[String]("editCode", O.NotNull)
	def viewCode = column[String]("viewCode", O.NotNull)
	def creationDate = column[DateTime]("creationDate", O.NotNull)
	def updateDate = column[DateTime]("updateDate", O.NotNull)

	def * = (id, name, owner, editCode, viewCode, creationDate, updateDate) <> (Course.tupled, Course.unapply _)

	def ownerFK = foreignKey("courses_owner_fk", owner, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)

//	def autoInc = name ~ owner ~ editCode ~ viewCode ~ creationDate ~ updateDate returning id

//	def insert(t: CourseTmp)(implicit s: Session) = this.autoInc.insert(t.name, t.owner, t.editCode, t.viewCode, t.date, t.date)
}