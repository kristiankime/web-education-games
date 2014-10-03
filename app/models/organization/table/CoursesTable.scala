package models.organization.table

import com.artclod.slick.JodaUTC._
import play.api.db.slick.Config.driver.simple._
import models.organization._
import models.support._
import org.joda.time.DateTime
import service.table.UsersTable
import scala.slick.model.ForeignKeyAction
import models.support.table.IdentifiedAndOwned

class CoursesTable(tag: Tag) extends Table[Course](tag, "courses") with IdentifiedAndOwned[Course, CourseId] {
	def id = column[CourseId]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name")
  def organization = column[OrganizationId]("organization")
	def owner = column[UserId]("owner")
	def editCode = column[String]("edit_code")
	def viewCode = column[String]("view_code")
	def creationDate = column[DateTime]("creation_Date")
	def updateDate = column[DateTime]("update_date")

	def * = (id, name, organization, owner, editCode, viewCode, creationDate, updateDate) <> (Course.tupled, Course.unapply _)

	def ownerFK = foreignKey("courses_owner_fk", owner, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)

  def organizationFK = foreignKey("courses_organization_fk", organization, organizationsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}