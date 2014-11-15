package controllers.question.derivative

import com.artclod.util._
import com.artclod.slick.JodaUTC
import play.api.db.slick.Config.driver.simple.Session
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Result, Controller}
import controllers.organization.CoursesController
import models.support._
import models.question.derivative._
import models.organization._
import controllers.support.{SecureSocialConsented, RequireAccess, SecureSocialDB}
import play.api.Logger
import service.Edit


object QuizzesController extends Controller with SecureSocialConsented {

  def apply(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId)(implicit session: Session) : Either[Result, (Organization, Course, Quiz)] =
    Quizzes(quizId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no quiz for id=["+quizId+"]")))
      case Some(quiz) => {
        quiz.course(courseId) match {
          case None => Left(NotFound(views.html.errors.notFoundPage("The course for id=[" + courseId + "] was not associated with the quiz for id=["+quizId+"]")))
          case Some(course) =>
            CoursesController(organizationId, courseId) + Right(quiz)
        }
      }
    }

  def createForm(organizationId: OrganizationId, courseId: CourseId) = ConsentedAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    CoursesController(organizationId, courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course)) => Ok(views.html.question.derivative.quizCreate(course))
    }
  }

  def createSubmit(organizationId: OrganizationId, courseId: CourseId) = ConsentedAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    CoursesController(organizationId, courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course)) =>
        QuizForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
              val now = JodaUTC.now
              val quiz = Quizzes.create(Quiz(null, user.id, form, now, now), course.id)
              Redirect(routes.QuizzesController.view(organization.id, course.id, quiz.id))
            })
    }
  }

  def view(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction(RequireAccess(courseId)) { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        val access = course.access
        Ok(views.html.question.derivative.quizView(access, course, quiz))
      }
    }
  }

  def rename(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) =>
        QuizForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            Quizzes.rename(quizId, form)
            Redirect(routes.QuizzesController.view(organization.id, course.id, quiz.id))
          })
    }
  }

}

object QuizForm {
  val name = "name"

  val values = Form(name -> nonEmptyText)
}