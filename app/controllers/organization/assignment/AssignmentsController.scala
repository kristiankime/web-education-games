package controllers.organization.assignment

import com.artclod.slick.Joda
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
            val now = Joda.now
            Assignments.create(Assignment(null, form.name, courseId, user.id, now, now, form.start, form.end))
            Redirect(controllers.organization.routes.CoursesController.view(courseId))
          })
    }
  }

  def view(courseId: CourseId, id: AssignmentId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    Assignments(id) match {
      case None => BadRequest(views.html.index())
      case Some(assignment) =>
        if (assignment.courseId != courseId) Redirect(routes.AssignmentsController.view(assignment.courseId, id))
        else Ok(assignmentView(assignment))
    }
  }

  def viewSection(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    (Sections(sectionId), Assignments(assignmentId)) match {
      case (Some(section), Some(assignment)) =>
        if (section.courseId != courseId || assignment.courseId != courseId) BadRequest(views.html.index())
        else Ok(assignmentSectionView(assignment, section))
      case _ => BadRequest(views.html.index())
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
