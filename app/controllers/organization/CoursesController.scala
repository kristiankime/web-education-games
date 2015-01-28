package controllers.organization

import play.api.db.slick.Config.driver.simple.Session
import scala.util.{Right, Random}
import com.artclod.random._
import com.artclod.slick.JodaUTC
import com.artclod.util._
import play.api.mvc.{Result, Controller}
import play.api.data.Form
import play.api.data.Forms._
import service._
import models.support._
import models.organization._
import controllers.support.SecureSocialConsented

object CoursesController extends Controller with SecureSocialConsented {
	implicit val randomEngine = new Random(JodaUTC.now.getMillis())
  val codeRange = (0 to 100000).toVector

  def apply(organizationId: OrganizationId, courseId: CourseId)(implicit session: Session) : Either[Result, (Organization, Course)] =
    Courses(courseId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no course for id=["+courseId+"]")))
      case Some(course) => {
        if(course.organizationId ^!= organizationId) Left(NotFound(views.html.errors.notFoundPage("organizationId=[" + organizationId +"] was not for courseId=["+courseId+"]")))
        else Right((course.organization, course))
      }
    }

	def list(organizationId: OrganizationId) = ConsentedAction { implicit request => implicit user => implicit session =>
    OrganizationsController(organizationId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(organization) => Ok(views.html.organization.courseList(organization, Courses.list(organization.id)))
    }
	}

	def createForm(organizationId: OrganizationId) = ConsentedAction { implicit request => implicit user => implicit session =>
    OrganizationsController(organizationId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(organization) => Ok(views.html.organization.courseCreate(organization))
    }
	}

	def createSubmit(organizationId: OrganizationId) = ConsentedAction { implicit request => implicit user => implicit session =>
    OrganizationsController(organizationId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(organization) =>
        CourseCreate.form.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val (editNum, viewNum) = codeRange.pick2From
            val now = JodaUTC.now
            val viewOp = if(form._2){ None } else { Some("CO-V-" + viewNum) }
            val course = Courses.create(Course(null, form._1, organization.id, user.id, "CO-E-" + editNum, viewOp, now, now))
            Redirect(routes.CoursesController.view(organization.id, course.id))
          })
    }
	}

	def view(organizationId: OrganizationId, courseId: CourseId) = ConsentedAction { implicit request => implicit user => implicit session =>
    CoursesController(organizationId, courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course)) => Ok(views.html.organization.courseView(organization, course))
    }
	}

	def join(organizationId: OrganizationId, courseId: CourseId) = ConsentedAction { implicit request => implicit user => implicit session =>
    CoursesController(organizationId, courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course)) => CourseJoin.form.bindFromRequest.fold(
        errors => BadRequest(views.html.errors.formErrorPage(errors)),
        form => {
            if (course.editCode == form) Courses.grantAccess(course, Edit)(user, session)
            if (course.viewCode == None || course.viewCode.get == form) Courses.grantAccess(course, View)(user, session)
            Redirect(routes.CoursesController.view(organization.id, course.id)) // TODO indicate access was not granted in a better fashion
        })
    }
	}

}

object CourseCreate {
	val name = "name"
  val anyStudents = "any_student"
	val form = Form(tuple(name -> nonEmptyText, anyStudents -> boolean))
}

object CourseJoin {
	val code = "code"
	val form = Form(code -> text)
}
