package controllers.question.derivative

import play.api.db.slick.Config.driver.simple.Session
import com.artclod.slick.JodaUTC
import com.artclod.mathml.MathML
import com.artclod.util._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Result, Controller}
import controllers.support.{SecureSocialConsented, SecureSocialDB}
import models.question.derivative._
import models.support._
import models.organization.Course

object QuestionsController extends Controller with SecureSocialConsented {

  def apply(quizId: QuizId, questionId: QuestionId)(implicit session: Session) : Either[Result, Question] =
    Questions(questionId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no question for id=["+questionId+"]")))
      case Some(question) => question.quiz match {
        case None => Left(NotFound(views.html.errors.notFoundPage("There was no quiz for question for id=["+questionId+"]")))
        case Some(quiz) =>
          if(quiz.id ^!= quizId) Left(NotFound(views.html.errors.notFoundPage("Question for id=["+questionId+"] is associated with quiz id=[" + quiz.id + "] not quiz id=[" + quizId + "]")))
          else Right(question)
      }
    }

	def view(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) +
    QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course , quiz, question)) => {
        val nextQuestion = quiz.results(user).nextQuestion(question)
        Answers.startWorkingOn(question.id)
        Ok(views.html.question.derivative.questionView(course, quiz, question.results(user), None))
      }
    }
	}

	def create(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        QuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val mathML = MathML(form._1).get // TODO better handle on error
            Questions.create(Question(null, user.id, mathML, form._2, JodaUTC.now), quizId)
            Redirect(routes.QuizzesController.view(organization.id, course.id, quiz.id))
          })
      }
    }
	}

	def remove(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) +
      QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question)) => {
        quiz.remove(question)
        Redirect(routes.QuizzesController.view(organization.id, course.id, quiz.id))
      }
    }
	}
}

object QuestionForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> text, rawStr -> text))
}
