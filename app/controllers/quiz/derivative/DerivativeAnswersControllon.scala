package controllers.quiz.derivative

import com.artclod.mathml.Match._
import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.quiz.{QuestionsController, QuizzesController}
import controllers.support.SecureSocialConsented
import models.quiz.answer.{DerivativeAnswerUnfinished, DerivativeAnswers}
import models.quiz.question.DerivativeQuestion
import models.support._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller

trait DerivativeAnswersControllon extends Controller with SecureSocialConsented {

  def createDerivative(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) + QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question : DerivativeQuestion)) => {
        DerivativeAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val (mathML, rawStr) = DerivativeAnswerForm(form)
            val unfinishedAnswer = DerivativeAnswerUnfinished(user.id, question.id, mathML, rawStr, JodaUTC.now)_
            DerivativeAnswers.correct(question, mathML) match {
              case Yes => Redirect(controllers.quiz.routes.QuizzesController.view(course.organizationId, course.id, quiz.id, Some(DerivativeAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case No => Redirect(controllers.quiz.routes.AnswersController.view(course.organizationId, course.id, quiz.id, question.id, DerivativeAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => Ok(views.html.quiz.derivative.questionView(course, quiz, question.results(user), Some(Left(unfinishedAnswer(false)))))
            }
          })
      }
      case Right((organization, course, quiz, _)) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + DerivativeQuestion.getClass.getSimpleName))
    }
  }

}

object DerivativeAnswerForm {
  val mathML = "mathML"
  val rawStr = "rawStr"
  val values = Form(tuple(mathML -> text, rawStr -> text))

  def apply(v : (String, String)) = (MathML(v._1).get, v._2) // TODO better error handling for .get
}