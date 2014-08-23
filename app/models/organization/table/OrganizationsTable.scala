package models.organization.table

import com.artclod.slick.JodaUTC._
import models.organization._
import models.support._
import models.support.table.IdentifiedAndOwned
import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime

class OrganizationsTable(tag: Tag) extends Table[Organization](tag, "organizations") { //with IdentifiedAndOwned[Course, CourseId] {
	def id = column[OrganizationId]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def creationDate = column[DateTime]("creation_Date", O.NotNull)
	def updateDate = column[DateTime]("update_date", O.NotNull)

	def * = (id, name, creationDate, updateDate) <> (Organization.tupled, Organization.unapply _)
}