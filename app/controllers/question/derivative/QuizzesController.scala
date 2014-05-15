package controllers.question.derivative

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import models.question.derivative._
import service._
import models.support._
import org.joda.time.DateTime
import models.organization.Courses
import controllers.support.SecureSocialDB
import models.organization.assignment.Groups

object QuizzesController extends Controller with SecureSocialDB {

	def add(courseIdOp: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
			Ok(views.html.question.derivative.quizAdd(courseIdOp.flatMap(Courses(_)), groupIdOp.flatMap(Groups(_))))
	}

	def create(courseIdOp: Option[CourseId], groupIdOp: Option[GroupId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
			QuizForm.values.bindFromRequest.fold(
				errors => BadRequest(views.html.index()),
				form => {
          (courseIdOp, groupIdOp) match {
            case (_, Some(groupId)) => {
              val quiz = Quizzes.create(QuizTmp(user.id, form, DateTime.now), groupId)
              Redirect(routes.QuizzesController.view(quiz.id, courseIdOp))
            }
            case (Some(courseId), _) => {
              val quiz = Quizzes.create(QuizTmp(user.id, form, DateTime.now), courseId)
              Redirect(routes.QuizzesController.view(quiz.id, courseIdOp))
            }
            case _ => BadRequest(views.html.index())
          }
				})
	}

	def view(quizId: QuizId, courseId: Option[CourseId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
			val courseOp = courseId.flatMap(Courses(_))
			val quizOp = Quizzes(quizId)
			val access = courseOp.map(_.access).getOrElse(Own) // TODO get access right

			(quizOp, courseOp) match {
				case (Some(quiz), Some(course)) => {
					val results = access.write(() => course.sectionResults(quiz))
					Ok(views.html.question.derivative.quizView(access, Some(course), quiz.results(user), results))
				}
				case (Some(quiz), None) => Ok(views.html.question.derivative.quizView(access, None, quiz.results(user), None))
				case _ => BadRequest(views.html.index())
			}
	}

	def rename(quizId: QuizId, courseId: Option[CourseId]) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
			QuizForm.values.bindFromRequest.fold(
				errors => BadRequest(views.html.index()),
				form => {
					Quizzes.rename(quizId, form)
					Redirect(routes.QuizzesController.view(quizId, courseId))
				})
	}

}

object QuizForm {
	val name = "name"

	val values = Form(name -> nonEmptyText)
}