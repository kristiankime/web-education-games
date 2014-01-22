package controllers.organization

import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import securesocial.core.SecureSocial
import service.User
import models.organization._
import models.question.derivative._
import models.id._
import org.joda.time.DateTime

object SectionsController extends Controller with SecureSocial {

	def list(courseId: CourseId) = TODO

	def add(courseId: CourseId) = SecuredAction { implicit request =>
		implicit val user = User(request)
		Courses.find(courseId) match {
			case Some(course) => Ok(views.html.organization.sectionAdd(course))
			case None => BadRequest(views.html.index())
		}
	}

	def create(courseId: CourseId) = SecuredAction { implicit request =>
		implicit val user = User(request)
		SectionForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				Sections.create(user, SectionTmp(form, courseId, DateTime.now))
				Redirect(routes.CoursesController.edit(courseId))
			})
	}

	def view(courseId: CourseId, id: SectionId) = TODO

	def edit(courseId: CourseId, id: SectionId) = TODO

	def update(courseId: CourseId, id: SectionId) = TODO

	def delete(courseId: CourseId, id: SectionId) = TODO
}

object SectionForm {
	val name = "name"

	val values = Form(name -> nonEmptyText)
}