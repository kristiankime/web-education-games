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

object QuizzesController extends Controller with SecureSocialDB {

  def add(courseIdOp: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val where = eitherOp(courseIdOp.flatMap(Courses(_)), groupIdOp.flatMap(Groups(_)))

    Ok(views.html.question.derivative.quizAdd(where))
  }

  def create(courseIdOp: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val where = eitherOp(courseIdOp.flatMap(Courses(_)), groupIdOp.flatMap(Groups(_)))

    QuizForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.index()),
      form => {
        (courseIdOp, groupIdOp) match {
          case (_, Some(groupId)) => {
            val quiz = Quizzes.create(QuizTmp(user.id, form, DateTime.now), groupId)
            Redirect(routes.QuizzesController.view(quiz.id, where.leftOp(_.id), where.rightOp(_.id)))
          }
          case (Some(courseId), _) => {
            val quiz = Quizzes.create(QuizTmp(user.id, form, DateTime.now), courseId)
            Redirect(routes.QuizzesController.view(quiz.id, where.leftOp(_.id), where.rightOp(_.id)))
          }
          case _ => BadRequest(views.html.index())
        }
      })
  }

  def view(quizId: QuizId, courseId: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val courseOp = courseId.flatMap(Courses(_))
    val quizOp = Quizzes(quizId)
    val access = courseOp.map(_.access).getOrElse(Own) // TODO get access right

    (quizOp, courseOp) match {
      case (Some(quiz), Some(course)) => {
        val results = access.write(() => course.sectionResults(quiz))
        Ok(views.html.question.derivative.quizView(access, Left(course), quiz.results(user), results))
      }
      //				case (Some(quiz), None) => Ok(views.html.question.derivative.quizView(access, None, quiz.results(user), None))
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