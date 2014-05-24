package controllers.organization.assignment

import com.artclod.slick.Joda
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc.Controller
import models.organization.assignment._
import models.organization._
import models.support._
import controllers.support._
import views.html.organization.assignment._
import controllers.support.SecureSocialDB
import service._

object GroupsController extends Controller with SecureSocialDB {

  def add(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId) = SecuredUserDBAction(RequireAccess(sectionId)) { implicit request => implicit user => implicit session =>
    (Sections(sectionId), Assignments(assignmentId)) match {
      case (Some(section), Some(assignment)) =>
        if (section.courseId == assignment.courseId) Ok(groupAdd(section.course, section, assignment))
        else NotFound(views.html.index())
      case _ => NotFound(views.html.index())
    }
  }

  def create(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId) = SecuredUserDBAction(RequireAccess(sectionId)) { implicit request => implicit user => implicit session =>
    (Sections(sectionId), Assignments(assignmentId)) match {
      case (Some(section), Some(assignment)) =>
        if (section.courseId == assignment.courseId)
          AssignmentCreate.form.bindFromRequest.fold(
            errors => BadRequest(views.html.index()),
            form => {
              val now = Joda.now
              Groups.create(Group(null, form.name, section.id, assignment.id, now, now))
              Redirect(routes.AssignmentsController.viewSection(assignment.courseId, section.id, assignment.id))
            })
        else NotFound(views.html.index())
      case _ => NotFound(views.html.index())
    }
  }

  def view(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, assignmentGroupId: GroupId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    (Sections(sectionId), Assignments(assignmentId), Groups(assignmentGroupId)) match {
      case (Some(section), Some(assignment), Some(group)) =>
        if (section.courseId == assignment.courseId) Ok(groupView(group))
        else NotFound(views.html.index())
      case _ => NotFound(views.html.index())
    }
  }

  def join(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, assignmentGroupId: GroupId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    (Sections(sectionId), Assignments(assignmentId), Groups(assignmentGroupId)) match {
      case (Some(section), Some(assignment), Some(group)) =>
        if (section.courseId == assignment.courseId && assignment.id == group.assignmentId) {
          joinGroup(group)
          Redirect(routes.GroupsController.view(section.courseId, section.id, assignment.id, group.id))
        }
        else NotFound(views.html.index())
      case _ => NotFound(views.html.index())
    }
  }

  private def joinGroup(group: Group)(implicit user: User, session: Session) =  {
    val assignment = group.assignment
    for(currentGroup <- assignment.enrolled){ currentGroup.leave } // leave a group if they are currently in one
    // TODO move quizzes to new group or disable quiz
    group.join
  }

}

object GroupCreate {
  val name = "name"
  val form = Form(name -> nonEmptyText)
}
