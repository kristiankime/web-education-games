package controllers.organization.assignment.quiz

import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import models.support._
import models.question.derivative._
import models.organization.assignment._
import controllers.support.SecureSocialDB

object GroupQuizzesController extends Controller with SecureSocialDB {

  def add(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val group = Groups(courseId, sectionId, assignmentId, groupId)
    Ok(views.html.organization.assignment.quiz.groupQuizAdd(group))
  }

  def create(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val group = Groups(courseId, sectionId, assignmentId, groupId)

    QuizForm.values.bindFromRequest.fold(
      errors => { BadRequest(views.html.errors.formErrorPage(errors)) },
      form => {
        val now = DateTime.now
        val quiz = Quizzes.create(Quiz(null, user.id, form, now, now), group.id)
        Redirect(routes.GroupQuizzesController.view(group.course.id, group.section.id, group.assignment.id, group.id, quiz.id))
      })
  }

  def view(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val group = Groups(courseId, sectionId, assignmentId, groupId)
    val quiz = Quizzes(groupId, quizId)
    Ok(views.html.organization.assignment.quiz.groupQuizView(group, quiz))
  }

  def rename(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val group = Groups(courseId, sectionId, assignmentId, groupId)
    val quiz = Quizzes(groupId, quizId)

    QuizForm.values.bindFromRequest.fold(
      errors => { BadRequest(views.html.errors.formErrorPage(errors)) },
      form => {
        quiz.rename(form)
        Redirect(routes.GroupQuizzesController.view(group.courseId, group.sectionId, group.assignmentId, group.id, quiz.id))
      })
  }

}

object QuizForm {
  val name = "name"

  val values = Form(name -> nonEmptyText)
}