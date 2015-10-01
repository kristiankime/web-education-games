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
import models.user.User
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
            val unfinishedAnswer = DerivativeAnswerForm.toAnswerUnfinished(user, question, form)
            DerivativeAnswers.correct(question, form.functionMathML) match {
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
  val function = "function"
  val functionStr = "functionStr"

  val values = Form(mapping(
    function -> text,
    functionStr -> text)
    (DerivativeAnswerForm.apply)(DerivativeAnswerForm.unapply))

  def toAnswerUnfinished(user: User, question: DerivativeQuestion, form: DerivativeAnswerForm) = DerivativeAnswerUnfinished(user.id, question.id, form.functionMathML, form.functionStr, "", JodaUTC.now)_
}

case class DerivativeAnswerForm(function: String, functionStr: String) {
  def functionMathML = MathML(function).get // TODO better error handling for .get
}