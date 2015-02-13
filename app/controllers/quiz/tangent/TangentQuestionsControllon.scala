package controllers.quiz.tangent

import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import controllers.quiz.QuizzesController
import controllers.support.SecureSocialConsented
import models.quiz.question.{QuestionDifficulty, TangentQuestion, TangentQuestions}
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
            val (function, functionStr, atPointX, atPointXStr) = (MathML(form._1).get, form._2, MathML(form._3).get, form._4) // TODO handle errors for .get
            TangentQuestions.create(TangentQuestion(null, user.id, function, functionStr, atPointX, atPointXStr, JodaUTC.now, QuestionDifficulty(function)), quizId) // TODO better handle on error for MathML().get
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