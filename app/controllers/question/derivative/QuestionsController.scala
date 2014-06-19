package controllers.question.derivative

import play.api.db.slick.Config.driver.simple.Session
import com.artclod.slick.Joda
import com.artclod.mathml.MathML
import com.artclod.util._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Result, Controller}
import controllers.support.SecureSocialDB
import models.question.derivative._
import models.support._
import models.organization.Course

object QuestionsController extends Controller with SecureSocialDB {

  def apply(quizId: QuizId, questionId: QuestionId)(implicit session: Session) : Either[Result, Question] =
    Questions(questionId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no question for id=["+questionId+"]")))
      case Some(question) => question.quiz match {
        case None => Left(NotFound(views.html.errors.notFoundPage("There was no quiz for question for id=["+questionId+"]")))
        case Some(quiz) =>
          if(quiz.id != quizId) Left(NotFound(views.html.errors.notFoundPage("Question for id=["+questionId+"] is associated with quiz id=[" + quiz.id + "] not quiz id=[" + quizId + "]")))
          else Right(question)
      }
    }

	def view(quizId: QuizId, questionId: QuestionId, courseId: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    QuizzesController(courseId, quizId) +
    QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((course : Course, quiz: Quiz, question: Question)) => {
        val nextQuestion = quiz.results(user).nextQuestion(question)
        Answers.startWorkingOn(question.id)
        Ok(views.html.question.derivative.questionView(quiz.access, course, quiz, question.results(user), None, nextQuestion))
      }
    }
	}

	def create(quizId: QuizId, courseId: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    QuizzesController(courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((course, quiz)) => {
        QuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val mathML = MathML(form._1).get // TODO better handle on error
            Questions.create(Question(null, user.id, mathML, form._2, Joda.now), quizId)
            Redirect(routes.QuizzesController.view(quiz.id, course.id))
          })
      }
    }
	}

	def remove(quizId: QuizId, questionId: QuestionId, courseId: CourseId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    QuizzesController(courseId, quizId) +
      QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((course, quiz, question)) => {
        quiz.remove(question)
        Redirect(routes.QuizzesController.view(quiz.id, course.id))
      }
    }
	}
}

object QuestionForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> text, rawStr -> text))
}
