package controllers.organization.assignment.quiz

import com.artclod.slick.Joda
import com.artclod.util._
import play.api.db.slick.Config.driver.simple.Session
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Result, Controller}
import models.support._
import models.question.derivative._
import controllers.support.SecureSocialDB
import controllers.organization.assignment.GroupsController

object GroupQuizzesController extends Controller with SecureSocialDB {

  def apply(groupId: GroupId, quizId: QuizId)(implicit session: Session) : Either[Result, Quiz] =
    Quizzes(quizId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no quiz for id=["+quizId+"]")))
      case Some(quiz) => quiz.group match {
        case None => Left(NotFound(views.html.errors.notFoundPage("There was no group associated with quiz for id=["+quizId+"]")))
        case Some(group) => {
          if(group.id !== groupId) Left(NotFound(views.html.errors.notFoundPage("quiz for with id=["+quizId+"] was associated with group id=[" +group.id+"] not group [" + groupId +"]")))
          Right(quiz)
        }
      }
    }

  def add(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    GroupsController(courseId, sectionId, assignmentId, groupId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(group) => Ok(views.html.organization.assignment.quiz.groupQuizAdd(group))
    }
  }

  def create(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    GroupsController(courseId, sectionId, assignmentId, groupId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(group) =>
        QuizForm.values.bindFromRequest.fold(
          errors => { BadRequest(views.html.errors.formErrorPage(errors)) },
          form => {
            val now = Joda.now
            val quiz = Quizzes.create(Quiz(null, user.id, form, now, now), group.id)
            Redirect(routes.GroupQuizzesController.view(group.course.id, group.section.id, group.assignment.id, group.id, quiz.id))
          })
    }
  }

  def view(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    GroupsController(courseId, sectionId, assignmentId, groupId) + GroupQuizzesController(groupId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((group, quiz)) => Ok(views.html.organization.assignment.quiz.groupQuizView(group, quiz))
    }
  }

  def rename(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    GroupsController(courseId, sectionId, assignmentId, groupId) + GroupQuizzesController(groupId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((group, quiz)) =>
        QuizForm.values.bindFromRequest.fold(
          errors => { BadRequest(views.html.errors.formErrorPage(errors)) },
          form => {
            quiz.rename(form)
            Redirect(routes.GroupQuizzesController.view(group.courseId, group.sectionId, group.assignmentId, group.id, quiz.id))
          })
    }
  }

}

object QuizForm {
  val name = "name"

  val values = Form(name -> nonEmptyText)
}