package controllers.quiz

import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.organization.CoursesController
import controllers.support.{RequireAccess, SecureSocialConsented}
import models.organization._
import models.quiz.answer.DerivativeAnswers
import models.quiz.{Quiz, Quizzes}
import models.support._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc.{Controller, Result}
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
      case Right((organization, course)) => Ok(views.html.quiz.quizCreate(course))
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
              Redirect(routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
            })
    }
  }

  def view(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, answerIdOp: Option[AnswerId]) = ConsentedAction(RequireAccess(courseId)) { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        val access = course.access
        Ok(views.html.quiz.quizView(access, course, quiz, answerIdOp.flatMap(id => DerivativeAnswers(id))))
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
            Redirect(routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
          })
    }
  }

}

object QuizForm {
  val name = "name"

  val values = Form(name -> nonEmptyText)
}