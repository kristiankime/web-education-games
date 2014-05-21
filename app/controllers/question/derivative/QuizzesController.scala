package controllers.question.derivative

import com.artclod.util._
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import service._
import models.support._
import models.question.derivative._
import models.organization.Courses
import models.organization.assignment.Groups
import controllers.support.SecureSocialDB

import play.api.Logger

object QuizzesController extends Controller with SecureSocialDB {

  def add(courseIdOp: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val where = eitherOp(courseIdOp.flatMap(Courses(_)), groupIdOp.flatMap(Groups(_)))

    Ok(views.html.question.derivative.quizAdd(where))
  }

  def create(courseIdOp: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val where = eitherOp(courseIdOp.flatMap(Courses(_)), groupIdOp.flatMap(Groups(_)))

    QuizForm.values.bindFromRequest.fold(
      errors => {
        Logger("create").info("error" + errors)
        BadRequest(views.html.errors.formErrorPage(errors))
      },
      form => where match {
          case Right(group) => {
            val now = DateTime.now
            val quiz = Quizzes.create(Quiz(null, user.id, form, now, now), group.id)
            Redirect(routes.QuizzesController.view(quiz.id, where.leftOp(_.id), where.rightOp(_.id)))
          }
          case Left(course) => {
            val now = DateTime.now
            val quiz = Quizzes.create(Quiz(null, user.id, form, now, now), course.id)
            Redirect(routes.QuizzesController.view(quiz.id, where.leftOp(_.id), where.rightOp(_.id)))
          }
        })
  }

  def view(quizId: QuizId, courseIdOp: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val where = eitherOp(courseIdOp.flatMap(Courses(_)), groupIdOp.flatMap(Groups(_)))
    val course = models.organization.courseFrom(where)
    val access = where.access

    Quizzes(quizId) match {
      case Some(quiz) => {
        val results = access.write(() => course.sectionResults(quiz))
        Ok(views.html.question.derivative.quizView(access, where, quiz.results(user), results))
      }
      case _ => BadRequest(views.html.index())
    }
  }

  def rename(quizId: QuizId, courseIdOp: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val where = eitherOp(courseIdOp.flatMap(Courses(_)), groupIdOp.flatMap(Groups(_)))

    QuizForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.index()),
      form => {
        Quizzes.rename(quizId, form)
        Redirect(routes.QuizzesController.view(quizId, where.leftOp(_.id), where.rightOp(_.id)))
      })
  }

}

object QuizForm {
  val name = "name"

  val values = Form(name -> nonEmptyText)
}