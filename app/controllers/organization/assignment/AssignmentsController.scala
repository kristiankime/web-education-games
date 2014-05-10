package controllers.organization.assignment

import org.joda.time.DateTime
import com.artclod.time._
import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import controllers.support._
import models.support._
import models.organization._
import models.organization.assignment._
import service.Edit
import views.html.organization.assignment._

object AssignmentsController extends Controller with SecureSocialDB {

  def add(courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    Courses(courseId) match {
      case Some(course) => Ok(assignmentAdd(course, AssignmentCreate.form.fill(AssignmentData())))
      case None => NotFound(views.html.index())
    }
  }

  def create(courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    Courses(courseId) match {
      case None => NotFound(views.html.index())
      case Some(course) =>
        AssignmentCreate.form.bindFromRequest.fold(
          errors => BadRequest(assignmentAdd(course, errors)),
          form => {
            Assignments.create(AssignmentTmp(form.name, courseId, user.id, DateTime.now, form.start, form.end))
            Redirect(controllers.organization.routes.CoursesController.view(courseId))
          })
    }
  }

  def view(courseId: CourseId, id: AssignmentId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    Assignments(id) match {
      case None => BadRequest(views.html.index())
      case Some(assignment) =>
        if (assignment.courseId != courseId) Redirect(routes.AssignmentsController.view(assignment.courseId, id))
        else Ok(assignmentView(assignment.details, assignment.sectionDetails))
    }
  }

  def viewSection(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
     Groups.details(sectionId, assignmentId) match {
      case None => BadRequest(views.html.index())
      case Some(details) =>
        if (details.section.courseId != courseId) Redirect(routes.AssignmentsController.viewSection(details.section.courseId, sectionId, assignmentId))
        else Ok(assignmentSectionView(details))
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
