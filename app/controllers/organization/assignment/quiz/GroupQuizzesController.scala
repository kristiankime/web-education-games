package controllers.organization.assignment.quiz

import com.artclod.util._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.Session
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Result, Controller}
import service._
import models.support._
import models.question.derivative._
import models.organization.Courses
import models.organization.assignment._
import controllers.support.SecureSocialDB

object GroupQuizzesController extends Controller with SecureSocialDB {

  private def groupCheck(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId)(implicit user: User, session: Session) : Option[Either[Group, Group]] =
    Groups(groupId) match {
    case None => None
    case Some(group) =>
      if (group.idsMatch(courseId, sectionId, assignmentId)) Some(Right(group))
      else Some(Left(group))
  }

  private def viewQuiz(group: Group, quiz: Quiz)(implicit user: User, session: Session) =
    if(group.has(quiz)) Ok(views.html.organization.assignment.quiz.groupQuizView(group.access, group, quiz.results(user)))
    else BadRequest(views.html.errors.notFoundPage("Group with id " + group.id + " did not contain quiz with id " + quiz.id))


  def add(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    groupCheck(courseId, sectionId, assignmentId, groupId) match {
      case None => BadRequest(views.html.errors.notFoundPage("Could not find the group with id " + groupId))
      case Some(Left(group)) => Redirect(routes.GroupQuizzesController.add(group.courseId, group.sectionId, group.assignmentId, groupId))
      case Some(Right(group)) => Ok(views.html.organization.assignment.quiz.groupQuizAdd(group))
    }
  }

  def create(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    Groups(groupId) match {
      case None => BadRequest(views.html.errors.notFoundPage("Could not find the group with id " + groupId))
      case Some(group) => {

        QuizForm.values.bindFromRequest.fold(
          errors => { BadRequest(views.html.errors.formErrorPage(errors)) },
          form => {
            val now = DateTime.now
            val quiz = Quizzes.create(Quiz(null, user.id, form, now, now), group.id)
            Redirect(routes.GroupQuizzesController.view(group.course.id, group.section.id, group.assignment.id, group.id, quiz.id))
          })

      }
    }
  }

  def view(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    (groupCheck(courseId, sectionId, assignmentId, groupId), Quizzes(quizId)) match {
      case (None, _) => BadRequest(views.html.errors.notFoundPage("Could not find the group with id " + groupId))
      case (_, None) => BadRequest(views.html.errors.notFoundPage("Could not find the quiz with id " + quizId))
      case (Some(Left(group)), _) => Redirect(routes.GroupQuizzesController.view(group.courseId, group.sectionId, group.assignmentId, groupId, quizId))
      case (Some(Right(group)), Some(quiz)) => viewQuiz(group, quiz)
    }
  }

  def rename(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    Quizzes(quizId) match {
      case None => BadRequest(views.html.errors.notFoundPage("Could not find the quiz with id " + quizId))
      case Some(quiz) => {
            QuizForm.values.bindFromRequest.fold(
              errors => { BadRequest(views.html.errors.formErrorPage(errors)) },
              form => {
                quiz.rename(form)
                Redirect(routes.GroupQuizzesController.view(courseId, sectionId, assignmentId, groupId, quizId))
              })
      }
    }
  }

}

object QuizForm {
  val name = "name"

  val values = Form(name -> nonEmptyText)
}