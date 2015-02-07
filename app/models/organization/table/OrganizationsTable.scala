package models.organization.table

import com.artclod.slick.JodaUTC._
import models.organization._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

class OrganizationsTable(tag: Tag) extends Table[Organization](tag, "organizations") {
	def id = column[OrganizationId]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name")
	def creationDate = column[DateTime]("creation_Date")
	def updateDate = column[DateTime]("update_date")

	def * = (id, name, creationDate, updateDate) <> (Organization.tupled, Organization.unapply _)
}