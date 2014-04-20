package controllers.organization

import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import controllers.support._
import models.support._
import models.organization._
import service.Edit
import org.joda.time.DateTime
import com.artclod.time._

object AssignmentsController extends Controller with SecureSocialDB {

  def add(courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId))  { implicit request => implicit user => implicit session =>
    Courses.find(courseId) match {
      case Some(course) => Ok(views.html.organization.assignmentAdd(course))
      case None => BadRequest(views.html.index(Courses.listDetails))
    }
  }

  def create(courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    AssignmentCreate.form.bindFromRequest.fold(
      errors => BadRequest(views.html.index(Courses.listDetails)),
      form => {
        Assignments.create(AssignmentTmp(form.name, courseId, user.id, DateTime.now, form.start, form.end))
        Redirect(routes.CoursesController.view(courseId))
      })
  }

  def view(courseId: CourseId, id: AssignmentId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    Assignments.find(id, courseId) match {
      case Some(assignment) => Ok(views.html.organization.assignmentView(assignment.details))
      case _ => BadRequest(views.html.index(Courses.listDetails))
    }
  }

}

case class AssignmentData(name: String, start: Option[java.util.Date], end: Option[java.util.Date])

object AssignmentCreate {
  val name = "name"
  val start = "start"
  val end = "end"
  val form = Form(
    mapping(name -> nonEmptyText,
    start -> optional(date),
      end -> optional(date))
      (AssignmentData.apply)(AssignmentData.unapply)
  )
}
