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
import controllers.support.SecureSocialDB

object SectionsController extends Controller with SecureSocialDB {
	val randomEngine = new Random(DateTime.now.getMillis())

	def list(courseId: CourseId) = TODO

	def add(courseId: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Courses.find(courseId) match {
			case Some(course) => Ok(views.html.organization.sectionAdd(course))
			case None => BadRequest(views.html.index(Courses.listDetails))
		}
	}

	def create(courseId: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		SectionCreate.form.bindFromRequest.fold(
			errors => BadRequest(views.html.index(Courses.listDetails)),
			form => {
				val editCode = "SE-E-" + randomEngine.nextInt(100000)
				val viewCode = "SE-V-" + randomEngine.nextInt(100000)
				Sections.create(SectionTmp(form, courseId, user.id, editCode, viewCode, DateTime.now))
				Redirect(routes.CoursesController.view(courseId))
			})
	}

	// TODO theoretically the wrong CourseId could be passed in here and this would still work
	def view(courseId: CourseId, id: SectionId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		(Courses.find(courseId), Sections.findDetails(id)) match {
			case (Some(course), Some(sectionDetails)) => Ok(views.html.organization.sectionView(course, sectionDetails, Quizzes.findByCourse(courseId)))
			case _ => BadRequest(views.html.index(Courses.listDetails))
		}
	}

	def join(courseId: CourseId, id: SectionId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		SectionJoin.form.bindFromRequest.fold(
			errors => BadRequest(views.html.index(Courses.listDetails)),
			form => {
				Sections.find(id) match {
					case Some(section) => {
						if (section.editCode == form) {
							Sections.grantAccess(section, Edit)
						} else if (section.viewCode == form) {
							Sections.grantAccess(section, View)
						}

						Redirect(routes.SectionsController.view(section.courseId, id))
					}
					case None => BadRequest(views.html.index(Courses.listDetails))
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
