package controllers.organization.assignment.quiz

import scala.util._
import play.api.db.slick.Config.driver.simple.Session
import com.artclod.util._
import com.artclod.slick.Joda
import com.artclod.mathml.MathML
import com.artclod.mathml.Match._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Result, Controller}
import models.support._
import models.question.derivative._
import controllers.support.SecureSocialDB
import controllers.organization.assignment.GroupsController

object GroupAnswersController extends Controller with SecureSocialDB {

  def apply(questionId: QuestionId, answerId: AnswerId)(implicit session: Session) : Either[Result, Answer] =
    Answers(answerId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no answer for id=["+answerId+"]")))
      case Some(answer) =>
        if(answer.id != answerId) Left(NotFound(views.html.errors.notFoundPage("The answer id=["+answerId+"] was not for the question id=[" + questionId + "]")))
        else Right(answer)
    }


	def view(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId, questionId: QuestionId, answerId: AnswerId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    GroupsController(courseId, sectionId, assignmentId, groupId) +
    GroupQuizzesController(groupId, quizId) +
    GroupQuestionsController(quizId, questionId) +
    GroupAnswersController(questionId, answerId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((group, quiz, question, answer)) => Ok(views.html.organization.assignment.quiz.groupQuestionView(group, quiz, question, Some(Right(answer))))
    }
	}

	def create(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId, questionId: QuestionId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    GroupsController(courseId, sectionId, assignmentId, groupId) +
    GroupQuizzesController(groupId, quizId) +
    GroupQuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((group, quiz, question)) =>
        AnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val math = MathML(form._1).get // TODO better error here
            val rawStr = form._2
            val aTmp = AnswerLater(user.id, question.id, math, rawStr, Joda.now)_

            Answers.correct(question, math) match {
              case Yes => Redirect(routes.GroupAnswersController.view(courseId, sectionId, assignmentId, groupId, quiz.id, question.id, Answers.createAnswer(aTmp(true)).id))
              case No => Redirect(routes.GroupAnswersController.view(courseId, sectionId, assignmentId, groupId, quiz.id, question.id, Answers.createAnswer(aTmp(false)).id))
              case Inconclusive => Ok(views.html.organization.assignment.quiz.groupQuestionView(group, quiz, question, Some(Left(aTmp(false)))))
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
