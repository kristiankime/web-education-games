package controllers.organization

import play.api.db.slick.Config.driver.simple.Session
import scala.util.Random
import com.artclod.random._
import com.artclod.slick.Joda
import play.api.mvc.{Result, Controller}
import play.api.data.Form
import play.api.data.Forms._
import service._
import models.support._
import models.organization._
import controllers.support.SecureSocialDB

object CoursesController extends Controller with SecureSocialDB {
	implicit val randomEngine = new Random(Joda.now.getMillis())
  val codeRange = (0 to 100000).toVector

  def apply(courseId: CourseId)(implicit session: Session) : Either[Result, Course] = Courses(courseId) match {
    case None => Left(NotFound(views.html.errors.notFoundPage("There was no course for id=["+courseId+"]")))
    case Some(course) => Right(course)
  }

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
        val now = Joda.now
				val course = Courses.create(Course(null, form, user.id, "CO-E-" + editNum, "CO-V-" + viewNum, now, now))
				Redirect(routes.CoursesController.view(course.id))
			})
	}

	def view(courseId: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    CoursesController(courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(course) => Ok(views.html.organization.courseView(course))
    }
	}

	def join(courseId: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    CoursesController(courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(course) => CourseJoin.form.bindFromRequest.fold(
        errors => BadRequest(views.html.index()),
        form => {
            if (course.viewCode == form) Courses.grantAccess(course, View)
            if (course.editCode == form) Courses.grantAccess(course, Edit)
            Redirect(routes.CoursesController.view(course.id)) // TODO indicate access was not granted in a better fashion
        })
    }
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
