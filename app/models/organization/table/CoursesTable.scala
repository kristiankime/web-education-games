package models.organization.table

import play.api.db.slick.Config.driver.simple._
import com.artclod.mathml._
import com.artclod.mathml.scalar._
import models._
import models.question.derivative._
import models.organization._
import models.support._
import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._
import service.table.UserTable
import scala.slick.lifted.ForeignKeyAction

class CoursesTable extends Table[Course]("courses") with IdentifiedAndOwned[Course, CourseId] {
	def id = column[CourseId]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def owner = column[UserId]("owner", O.NotNull)
	def editCode = column[String]("editCode", O.NotNull)
	def viewCode = column[String]("viewCode", O.NotNull)
	def creationDate = column[DateTime]("creationDate", O.NotNull)
	def updateDate = column[DateTime]("updateDate", O.NotNull)

	def * = id ~ name ~ owner ~ editCode ~ viewCode ~ creationDate ~ updateDate <> (Course, Course.unapply _)

	def ownerFK = foreignKey("courses_owner_fk", owner, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)

	def autoInc = name ~ owner ~ editCode ~ viewCode ~ creationDate ~ updateDate returning id

	def insert(t: CourseTmp)(implicit s: Session) = this.autoInc.insert(t.name, t.owner, t.editCode, t.viewCode, t.date, t.date)
}
