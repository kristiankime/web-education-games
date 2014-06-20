package controllers.organization.assignment

import com.artclod.slick.Joda
import com.artclod.util._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc.Controller
import models.organization.assignment._
import models.organization._
import models.support._
import controllers.support._
import views.html.organization.assignment._
import controllers.support.SecureSocialDB
import service._
import play.api.mvc.Result
import controllers.organization.SectionsController

object GroupsController extends Controller with SecureSocialDB {

  def apply(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId)(implicit session: Session) : Either[Result, Group] =
    Groups(groupId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no group for id=["+groupId+"]")))
      case Some(group) => {
        if(courseId  ^!= group.courseId) Left(NotFound(views.html.errors.notFoundPage("courseId=[" + courseId +"] was not for groupId=["+groupId+"]")))
        if(sectionId ^!= group.sectionId) Left(NotFound(views.html.errors.notFoundPage("sectionId=[" + sectionId +"] was not for groupId=["+groupId+"]")))
        if(assignmentId ^!= group.assignmentId) Left(NotFound(views.html.errors.notFoundPage("assignmentId=[" + assignmentId +"] was not for groupId=["+groupId+"]")))
        Right(group)
      }
    }

  def add(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId) = SecuredUserDBAction(RequireAccess(sectionId)) { implicit request => implicit user => implicit session =>
    SectionsController(courseId, sectionId) + AssignmentsController(courseId, assignmentId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((section, assignment)) => Ok(groupAdd(section.course, section, assignment))
    }
  }

  def create(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId) = SecuredUserDBAction(RequireAccess(sectionId)) { implicit request => implicit user => implicit session =>
    SectionsController(courseId, sectionId) + AssignmentsController(courseId, assignmentId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((section, assignment)) =>
        AssignmentCreate.form.bindFromRequest.fold(
          errors => BadRequest(views.html.index()),
          form => {
            val now = Joda.now
            Groups.create(Group(null, form.name, section.id, assignment.id, now, now))
            Redirect(routes.AssignmentsController.viewSection(assignment.courseId, section.id, assignment.id))
          })
    }
  }

  def view(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    GroupsController(courseId, sectionId, assignmentId, groupId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(group) => Ok(groupView(group))
    }
  }

  def join(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    SectionsController(courseId, sectionId) + AssignmentsController(courseId, assignmentId) + GroupsController(courseId, sectionId, assignmentId, groupId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((section, assignment, group)) =>  {
        joinGroup(group)
        Redirect(routes.GroupsController.view(section.courseId, section.id, assignment.id, group.id))
      }
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
