package models.organization.table

import com.artclod.slick.JodaUTC._
import models.organization._
import models.support._
import models.support.table.IdentifiedAndOwned
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable

import scala.slick.model.ForeignKeyAction

class CoursesTable(tag: Tag) extends Table[Course](tag, "courses") with IdentifiedAndOwned[Course, CourseId] {
	def id = column[CourseId]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name")
  def organization = column[OrganizationId]("organization")
	def owner = column[UserId]("owner")
	def editCode = column[String]("edit_code")
	def viewCode = column[Option[String]]("view_code")
	def creationDate = column[DateTime]("creation_Date")
	def updateDate = column[DateTime]("update_date")

	def * = (id, name, organization, owner, editCode, viewCode, creationDate, updateDate) <> (Course.tupled, Course.unapply _)

	def ownerFK = foreignKey("courses__owner_fk", owner, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)

  def organizationFK = foreignKey("courses__organization_fk", organization, organizationsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}