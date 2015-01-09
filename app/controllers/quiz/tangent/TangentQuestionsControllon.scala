package controllers.quiz.tangent

import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import controllers.quiz.QuizzesController
import controllers.support.SecureSocialConsented
import models.quiz.question.{TangentQuestions, TangentQuestion}
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
            val (function, functionStr, atPointX, atPointXStr) = form
            TangentQuestions.create(TangentQuestion(null, user.id, MathML(function).get, functionStr, MathML(atPointX).get, atPointXStr, JodaUTC.now), quizId) // TODO better handle on error for MathML().get
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
  val values = Form(tuple(function -> nonEmptyText, functionStr -> nonEmptyText, atPointX -> nonEmptyText, atPointXStr -> nonEmptyText))
}