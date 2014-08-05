package models.organization

import com.artclod.slick.JodaUTC
import models.organization.table._
import models.support.{CourseId, OrganizationId}
import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime

case class Organization(id: OrganizationId, name: String, creationDate: DateTime, updateDate: DateTime)

object Organizations {

  def brandeis(implicit session: Session) = {
    val name = "Brandeis University"

    Organizations(name) match {
      case None => {val now = JodaUTC.now; create(Organization(null, name, now, now)) }
      case Some(deis) => deis
    }
  }

  // ======= CREATE ======
  def create(organization: Organization)(implicit session: Session) = {
    val organizationId = (organizationsTable returning organizationsTable.map(_.id)) += organization
    organization.copy(id = organizationId)
  }

  // ======= FIND ======
  def apply(id: OrganizationId)(implicit session: Session) = organizationsTable.where(_.id === id).firstOption

  def apply(name: String)(implicit session: Session) = organizationsTable.where(_.name === name).firstOption

}
