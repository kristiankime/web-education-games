package controllers.question.derivative

import com.artclod.slick.Joda
import play.api.db.slick.Config.driver.simple.Session
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Result, Controller}
import controllers.organization.CoursesController
import models.support._
import models.question.derivative._
import models.organization.Course
import controllers.support.{RequireAccess, SecureSocialDB}
import play.api.Logger
import service.Edit


object QuizzesController extends Controller with SecureSocialDB {

  def apply(courseId: CourseId, quizId: QuizId)(implicit session: Session) : Either[Result, (Course, Quiz)] =
    Quizzes(quizId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no quiz for id=["+quizId+"]")))
      case Some(quiz) => {
        quiz.course match {
          case None => Left(NotFound(views.html.errors.notFoundPage("There was no course for the quiz for id=["+quizId+"]")))
          case Some(course) =>
            if(quiz.course != courseId) Left(NotFound(views.html.errors.notFoundPage("quizId=[" + courseId +"] was not for courseId=["+courseId+"]")))
            else Right((course, quiz))
        }
      }
    }

  def add(courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    CoursesController(courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(course) => Ok(views.html.question.derivative.quizAdd(course))
    }
  }

  def create(courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    CoursesController(courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(course) =>
        QuizForm.values.bindFromRequest.fold(
          errors => {
            Logger("create").info("error" + errors)
            BadRequest(views.html.errors.formErrorPage(errors))
          },
          form => {
              val now = Joda.now
              val quiz = Quizzes.create(Quiz(null, user.id, form, now, now), course.id)
              Redirect(routes.QuizzesController.view(quiz.id, course.id))
            })
    }
  }

  def view(quizId: QuizId, courseId: CourseId) = SecuredUserDBAction(RequireAccess(courseId)) { implicit request => implicit user => implicit session =>
    QuizzesController(courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((course, quiz)) => {
        val access = course.access
        val sectionResultsOp = access.write(() => course.sectionResults(quiz))
        Ok(views.html.question.derivative.quizView(course.access, course, quiz.results(user), sectionResultsOp))
      }
    }
  }

  def rename(quizId: QuizId, courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    QuizzesController(courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((course, quiz)) =>
        QuizForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.index()),
          form => {
            Quizzes.rename(quizId, form)
            Redirect(routes.QuizzesController.view(quizId, courseId))
          })
    }
  }

}

object QuizForm {
  val name = "name"

  val values = Form(name -> nonEmptyText)
}