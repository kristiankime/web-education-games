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
import service._
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
		SectionCreate.form.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				val editCode = "SE-E-" + randomEngine.nextInt(100000)
				val viewCode = "SE-V-" + randomEngine.nextInt(100000)
				Sections.create(SectionTmp(form, courseId, user.id, editCode, viewCode, DateTime.now))
				Redirect(routes.CoursesController.view(courseId))
			})
	}

	// TODO theoretically the wrong CourseId could be passed in here and this would still work
	def view(courseId: CourseId, id: SectionId) = SecuredAction { implicit request =>
		implicit val user = User(request)
		(Courses.find(courseId), Sections.findDetails(id)) match {
			case (Some(course), Some(sectionDetails)) => Ok(views.html.organization.sectionView(course, sectionDetails, Quizzes.findByCourse(courseId)))
			case _ => BadRequest(views.html.index())
		}
	}

	def update(courseId: CourseId, id: SectionId) = TODO

	def delete(courseId: CourseId, id: SectionId) = TODO

	def join(courseId: CourseId, id: SectionId) = SecuredAction { implicit request =>
		implicit val user = User(request)

		SectionJoin.form.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				Sections.find(id) match {
					case Some(section) => {
						if (section.editCode == form) {
							Sections.grantAccess(user, section, Edit)
						} else if (section.viewCode == form) {
							Sections.grantAccess(user, section, View)
						}

						Redirect(routes.SectionsController.view(section.courseId, id))
					}
					case None => BadRequest(views.html.index())
				}
			})
	}

}

object SectionCreate {
	val name = "name"
	val form = Form(name -> nonEmptyText)
}

object SectionJoin {
	val code = "code"
	val form = Form(code -> text)
}
