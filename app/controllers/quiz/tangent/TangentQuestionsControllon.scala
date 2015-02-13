package controllers.quiz.tangent

import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import controllers.quiz.QuizzesController
import controllers.support.SecureSocialConsented
import models.quiz.question.{TangentQuestionHelper, QuestionDifficulty, TangentQuestion, TangentQuestions}
import models.support._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller

trait TangentQuestionsControllon extends Controller with SecureSocialConsented {

  def createTangent(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        TangentQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            TangentQuestions.create(TangentQuestionHelper.fromForm(user, form), quizId)
            Redirect(controllers.quiz.routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
          })
      }
    }
  }

}

object TangentQuestionForm {
  val function = "function"
  val functionStr = "functionStr"
  val atPointX = "atPointX"
  val atPointXStr = "atPointXStr"

  val values = Form(mapping(
    function -> nonEmptyText,
    functionStr -> nonEmptyText,
    atPointX -> nonEmptyText,
    atPointXStr -> nonEmptyText)
    (TangentQuestionForm.apply)(TangentQuestionForm.unapply))
}

case class TangentQuestionForm(function: String, functionStr : String, atPointX: String, atPointXStr: String) {
  // TODO handle errors for .get
  def functionMathML = MathML(function).get
  def atPointXMathML = MathML(atPointX).get
}