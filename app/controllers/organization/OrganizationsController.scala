package controllers.organization

import controllers.support.SecureSocialConsented
import models.organization._
import models.support._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc._

object OrganizationsController extends Controller with SecureSocialConsented {

  def apply(organizationId: OrganizationId)(implicit session: Session) : Either[Result, Organization] = Organizations(organizationId) match {
    case None => Left(NotFound(views.html.errors.notFoundPage("There was no organization for id=["+organizationId+"]")))
    case Some(organization) => Right(organization)
  }

  def list = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.organization.organizationList(Organizations.list))
  }

  def view(organizationId: OrganizationId) = ConsentedAction { implicit request => implicit user => implicit session =>
    OrganizationsController(organizationId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(organization) => Ok(views.html.organization.organizationView(organization))
    }
  }

}
