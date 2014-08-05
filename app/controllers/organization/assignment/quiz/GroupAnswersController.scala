package controllers.organization.assignment.quiz

import controllers.question.derivative.{QuestionsController, AnswersController}

import scala.util._
import com.artclod.util._
import com.artclod.slick.JodaUTC
import com.artclod.mathml.MathML
import com.artclod.mathml.Match._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import models.support._
import models.question.derivative._
import controllers.support.SecureSocialDB
import controllers.organization.assignment.GroupsController

object GroupAnswersController extends Controller with SecureSocialDB {


	def view(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId, questionId: QuestionId, answerId: AnswerId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    GroupsController(courseId, sectionId, assignmentId, groupId) +
    GroupQuizzesController(groupId, quizId) +
    QuestionsController(quizId, questionId) +
    AnswersController(questionId, answerId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((group, quiz, question, answer)) => Ok(views.html.organization.assignment.quiz.groupQuestionView(group, quiz, question, Some(Right(answer))))
    }
	}

	def create(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId, questionId: QuestionId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    GroupsController(courseId, sectionId, assignmentId, groupId) +
    GroupQuizzesController(groupId, quizId) +
    QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((group, quiz, question)) =>
        AnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val math = MathML(form._1).get // TODO better error here
            val rawStr = form._2
            val unfinishedAnswer = UnfinishedAnswer(user.id, question.id, math, rawStr, JodaUTC.now)_

            Answers.correct(question, math) match {
              case Yes => Redirect(routes.GroupAnswersController.view(courseId, sectionId, assignmentId, groupId, quiz.id, question.id, Answers.createAnswer(unfinishedAnswer(true)).id))
              case No => Redirect(routes.GroupAnswersController.view(courseId, sectionId, assignmentId, groupId, quiz.id, question.id, Answers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => Ok(views.html.organization.assignment.quiz.groupQuestionView(group, quiz, question, Some(Left(unfinishedAnswer(false)))))
            }
          })
    }
	}

}

object AnswerForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> text, rawStr -> text))
}
