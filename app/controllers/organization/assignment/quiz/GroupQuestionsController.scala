package controllers.organization.assignment.quiz

import controllers.question.derivative.QuestionsController
import com.artclod.util._
import com.artclod.slick.Joda
import com.artclod.mathml.MathML
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import controllers.support.SecureSocialDB
import models.support._
import models.question.derivative._
import controllers.organization.assignment.GroupsController

object GroupQuestionsController extends Controller with SecureSocialDB {

	def view(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId, questionId: QuestionId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    GroupsController(courseId, sectionId, assignmentId, groupId) +
    GroupQuizzesController(groupId, quizId) +
    QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((group, quiz, question)) => Ok(views.html.organization.assignment.quiz.groupQuestionView(group, quiz, question, None))
    }
	}

	def create(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    GroupsController(courseId, sectionId, assignmentId, groupId) +
    GroupQuizzesController(groupId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((group, quiz)) =>
        QuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val mathML = MathML(form._1).get // TODO better handle on error
            Questions.create(Question(null, user.id, mathML, form._2, Joda.now), groupId, quiz.id, UserId(form._3))
            Redirect(routes.GroupQuizzesController.view(group.courseId, group.sectionId, group.assignmentId, group.id, quiz.id))
          })
    }
	}

	def remove(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId, questionId: QuestionId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    GroupsController(courseId, sectionId, assignmentId, groupId) +
    GroupQuizzesController(groupId, quizId) +
    QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((group, quiz, question)) => {
        quiz.remove(question)
        Redirect(routes.GroupQuizzesController.view(group.courseId, group.sectionId, group.assignmentId, group.id, quiz.id))
      }
    }
	}

}

object QuestionForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
  val targetUser = "targetUser"

	val values = Form(tuple(mathML -> text, rawStr -> text, targetUser -> longNumber))
}
