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
import service.Access
import scala.util.Random

object SectionsController extends Controller with SecureSocial {
	val randomEngine = new Random(0L)
	
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
				val editCode = "SE-E-" + randomEngine.nextInt(100000)
				val viewCode = "SE-V-" + randomEngine.nextInt(100000)
				Sections.create(SectionTmp(form, courseId, user.id, editCode, viewCode, DateTime.now))
				Redirect(routes.CoursesController.view(courseId))
			})
	}

	def view(courseId: CourseId, id: SectionId) = TODO

	def update(courseId: CourseId, id: SectionId) = TODO

	def delete(courseId: CourseId, id: SectionId) = TODO
	
	def enroll(courseId: CourseId, id: SectionId) = SecuredAction { implicit request =>
		implicit val user = User(request)
		Sections.find(id) match {
			case Some(section) => {
				Sections.enroll(user, section)
				Redirect(routes.CoursesController.view(courseId))
			}
			case None => BadRequest(views.html.index())
		}
	}
}

object SectionForm {
	val name = "name"

	val values = Form(name -> nonEmptyText)
}