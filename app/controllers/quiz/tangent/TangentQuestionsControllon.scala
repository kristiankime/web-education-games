package controllers.quiz.tangent

import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import controllers.quiz.QuizzesController
import controllers.support.SecureSocialConsented
import models.quiz.question.{QuestionDifficulty, TangentQuestion, TangentQuestions}
import models.support._
import models.user.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller

trait TangentQuestionsControllon extends Controller with SecureSocialConsented {

  def createTangent(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        TangentQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.quiz.quizView(course.access, course, quiz, None, errors)),
          form => {
            TangentQuestions.create(TangentQuestionForm.toQuestion(user, form), quizId)
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

  val tangentUndefined = "tangentUndefined"

  val values = Form(
    mapping(function -> nonEmptyText.verifying(f => MathML(f).isSuccess),
            functionStr -> nonEmptyText,
            atPointX -> nonEmptyText.verifying(f => MathML(f).isSuccess),
            atPointXStr -> nonEmptyText)
    (TangentQuestionForm.apply)(TangentQuestionForm.unapply)
    verifying(tangentUndefined, fields => tangentDefined(fields) )
  )

  def toQuestion(user: User, form: TangentQuestionForm) = TangentQuestion(null, user.id, form.functionMathML, form.functionStr, form.atPointXMathML, form.atPointXStr, JodaUTC.now, QuestionDifficulty(form.functionMathML))

  def tangentDefined(f: TangentQuestionForm) = f.functionMathML.isDefinedAt("x" -> f.atPointXMathML.evalT().get)
}

case class TangentQuestionForm(function: String, functionStr : String, atPointX: String, atPointXStr: String) {
  // TODO handle errors for .get
  def functionMathML = MathML(function).get
  def atPointXMathML = MathML(atPointX).get
}