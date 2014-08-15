package controllers.organization

import controllers.support.SecureSocialConsented
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import models.organization._
import models.support._
import play.api.mvc._
import service.{Edit, View}

object GamesController extends Controller with SecureSocialConsented {

  def findPlayer(organizationId: OrganizationId, courseId: CourseId)  = ConsentedAction { implicit request => implicit user => implicit session =>
    CoursesController(organizationId, courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course)) => Ok(views.html.organization.gameFindPlayer(organization, course))
    }
  }

  def requestGame(organizationId: OrganizationId, courseId: CourseId) = ConsentedAction { implicit request => implicit user => implicit session =>
    CoursesController(organizationId, courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course)) => GameRequest.form.bindFromRequest.fold(
        errors => BadRequest(views.html.index()),
        form => {
          Games.requestCourse(user.id, form, course.id)
          Redirect(routes.CoursesController.view(organization.id, course.id))
        })
    }
  }

}

object GameRequest {
  val requestee = "requestee"
  val form = Form(requestee -> userId)
}
