package controllers.organization

import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import org.joda.time.DateTime
import models.organization.assignment._
import models.organization._
import models.support._
import controllers.support._
import service._
import views.html.organization.assignment._
import com.artclod.scala._

object AssignmentGroupsController extends Controller with SecureSocialDB {

  def add(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId) = SecuredUserDBAction(RequireAccess(sectionId)) { implicit request => implicit user => implicit session =>
      (Sections.find(sectionId), Assignments.find(assignmentId)) match {
        case (Some(section), Some(assignment)) =>
            if(section.courseId == assignment.courseId) Ok(groupAdd(section.course, section, assignment))
            else NotFound(views.html.index(Courses.listDetails))
        case _ => NotFound(views.html.index(Courses.listDetails))
      }
  }

  def create(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId) = SecuredUserDBAction(RequireAccess(sectionId)) { implicit request => implicit user => implicit session =>
    (Sections.find(sectionId), Assignments.find(assignmentId)) match {
      case (Some(section), Some(assignment)) =>
        if(section.courseId == assignment.courseId)
          AssignmentCreate.form.bindFromRequest.fold(
            errors => BadRequest(views.html.index(Courses.listDetails)),
            form => {
              val now = DateTime.now
              AssignmentGroups.create(AssignmentGroupTmp(form.name, section.id, assignment.id, now, now))
              Redirect(routes.AssignmentsController.view(assignment.courseId, assignment.id))
            })
        else NotFound(views.html.index(Courses.listDetails))
      case _ => NotFound(views.html.index(Courses.listDetails))
    }
  }

  def view(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, assignmentGroupId : AssignmentGroupId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    (Sections.find(sectionId), Assignments.find(assignmentId), AssignmentGroups.find(assignmentGroupId)) match {
      case (Some(section), Some(assignment), Some(group)) =>
        if(section.courseId == assignment.courseId) Ok(groupView(assignment.details, section, group.details))
        else NotFound(views.html.index(Courses.listDetails))
      case _ => NotFound(views.html.index(Courses.listDetails))
    }
  }

}

object GroupCreate {
  val name = "name"
  val form = Form(name -> nonEmptyText)
}
