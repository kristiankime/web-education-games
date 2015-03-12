package controllers.quiz.derivative

import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import controllers.quiz.QuizzesController
import controllers.support.SecureSocialConsented
import models.quiz.question.{DerivativeQuestionDifficulty, DerivativeQuestion, DerivativeQuestions, DerivativeDifficulty}
import models.support._
import models.user.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller

import scala.util.Left

trait DerivativeQuestionsControllon extends Controller with SecureSocialConsented {

  def createDerivative(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        DerivativeQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            DerivativeQuestions.create(DerivativeQuestionForm.toQuestion(user, form), quiz.id)
            Redirect(controllers.quiz.routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
          })
      }
    }
  }

}

object DerivativeQuestionForm {
  val function = "function"
  val functionStr = "functionStr"

  val values = Form(mapping(
    function -> nonEmptyText,
    functionStr -> nonEmptyText)
    (DerivativeQuestionForm.apply)(DerivativeQuestionForm.unapply))

  def toQuestion(user: User, form: DerivativeQuestionForm) = DerivativeQuestion(null, user.id, form.functionMathML, form.functionStr, JodaUTC.now, DerivativeQuestionDifficulty(form.functionMathML))
}

case class DerivativeQuestionForm(function: String, functionStr: String) {
  def functionMathML = MathML(function).get // TODO better handle on error for .get
}