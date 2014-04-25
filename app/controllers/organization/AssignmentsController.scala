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
import models.organization.assignment.{Assignments, AssignmentTmp}
import views.html.organization.assignment._

object AssignmentsController extends Controller with SecureSocialDB {

  def add(courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    Courses.find(courseId) match {
      case Some(course) => Ok(assignmentAdd(course, AssignmentCreate.form.fill(AssignmentData())))
      case None => NotFound(views.html.index(Courses.listDetails))
    }
  }

  def create(courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    Courses.find(courseId) match {
      case None => NotFound(views.html.index(Courses.listDetails))
      case Some(course) =>
        AssignmentCreate.form.bindFromRequest.fold(
          errors => BadRequest(assignmentAdd(course, errors)),
          form => {
            Assignments.create(AssignmentTmp(form.name, courseId, user.id, DateTime.now, form.start, form.end))
            Redirect(routes.CoursesController.view(courseId))
          })
    }
  }

  def view(courseId: CourseId, id: AssignmentId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    Assignments.find(id) match {
      case None => BadRequest(views.html.index(Courses.listDetails))
      case Some(assignment) =>
        if (assignment.courseId != courseId) Redirect(routes.AssignmentsController.view(assignment.courseId, id))
        else Ok(assignmentView(assignment.details))
    }
  }

}

case class AssignmentData(name: String = "Assignment Name", start: Option[java.util.Date] = None, end: Option[java.util.Date] = None)

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
