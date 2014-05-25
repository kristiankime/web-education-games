package controllers.organization.assignment

import play.api.db.slick.Config.driver.simple._
import play.api.mvc.{Result, Controller}
import play.api.data.Form
import play.api.data.Forms._
import com.artclod.slick.Joda
import com.artclod.time._
import com.artclod.util._
import controllers.support._
import models.support._
import models.organization._
import models.organization.assignment._
import service.Edit
import views.html.organization.assignment._
import controllers.organization.{CoursesController, SectionsController}

object AssignmentsController extends Controller with SecureSocialDB {

  def apply(courseId: CourseId, assignmentId: AssignmentId)(implicit session: Session) : Either[Result, Assignment] = {
    Assignments(assignmentId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no assignment for id=["+assignmentId+"]")))
      case Some(assignment) => {
        if(courseId != assignment.courseId) Left(NotFound(views.html.errors.notFoundPage("courseId=[" + courseId +"] was not for assignmentId=["+assignmentId+"]")))
        Right(assignment)
      }
    }
  }

  def add(courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    CoursesController(courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(course) => Ok(views.html.organization.assignment.assignmentAdd(course, AssignmentCreate.form.fill(AssignmentData())))
    }
  }

  def create(courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    CoursesController(courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(course) =>
        AssignmentCreate.form.bindFromRequest.fold(
          errors => BadRequest(views.html.organization.assignment.assignmentAdd(course, errors)),
          form => {
            val now = Joda.now
            Assignments.create(Assignment(null, form.name, courseId, user.id, now, now, form.start, form.end))
            Redirect(controllers.organization.routes.CoursesController.view(courseId))
          })
    }
  }

  def view(courseId: CourseId, assignmentId: AssignmentId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    AssignmentsController(courseId, assignmentId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(assignment) => Ok(views.html.organization.assignment.assignmentView(assignment))
    }
  }

  def viewSection(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    SectionsController(courseId, sectionId) + AssignmentsController(courseId, assignmentId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((section, assignment)) => Ok(assignmentSectionView(assignment, section))
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
