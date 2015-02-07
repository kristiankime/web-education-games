package models.organization

import com.artclod.slick.JodaUTC
import models.organization.table._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

case class Organization(id: OrganizationId, name: String, creationDate: DateTime, updateDate: DateTime) {
  def courses(implicit session: Session) = Organizations.courses(id)
}

object Organizations {

  def list(implicit session: Session) = List(brandeis)

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

  def courses(id: OrganizationId)(implicit session: Session) = coursesTable.where(_.organization === id).sortBy(_.name).list
}
