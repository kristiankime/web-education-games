package controllers.organization

import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import scala.util.Random
import service._
import models.support._
import models.organization._
import models.question.derivative._
import controllers.support.SecureSocialDB
import org.joda.time.DateTime
import com.artclod.random._
import models.organization.assignment.Assignments

object CoursesController extends Controller with SecureSocialDB {
	implicit val randomEngine = new Random(DateTime.now.getMillis())
  val codeRange = (0 to 100000).toVector

	def list = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Ok(views.html.organization.courseList(Courses.list))
	}

	def add = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Ok(views.html.organization.courseAdd())
	}

	def create = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		CourseCreate.form.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
        val (editNum, viewNum) = codeRange.pick2From
        val now = DateTime.now
				val course = Courses.create(Course(null, form, user.id, "CO-E-" + editNum, "CO-V-" + viewNum, now, now))
				Redirect(routes.CoursesController.view(course.id))
			})
	}

	def view(id: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		Courses(id) match {
			case Some(course) => Ok(views.html.organization.courseView(course))
			case None => NotFound(views.html.index())
		}
	}

	def join(id: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		CourseJoin.form.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => Courses(id) match {
                case Some(course) => {
                  if (course.viewCode == form) Courses.grantAccess(course, View)
                  if (course.editCode == form) Courses.grantAccess(course, Edit)
                  Redirect(routes.CoursesController.view(course.id)) // TODO indicate acesss was not granted in a better fashion
                }
                case None => NotFound(views.html.index())
              })
	}

}

object CourseCreate {
	val name = "name"
	val form = Form(name -> nonEmptyText)
}

object CourseJoin {
	val code = "code"
	val form = Form(code -> text)
}
