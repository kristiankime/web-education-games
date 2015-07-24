package controllers.quiz.tangent

import com.artclod.mathml.MathML
import com.artclod.slick.JodaUTC
import controllers.quiz.QuizzesController
import controllers.quiz.derivative.DerivativeQuestionForm
import controllers.quiz.derivativegraph.DerivativeGraphQuestionForm
import controllers.support.SecureSocialConsented
import models.quiz.question._
import models.support._
import models.user.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

import scala.util.{Success, Failure}

trait TangentQuestionsControllon extends Controller with SecureSocialConsented {

  def createTangent(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        TangentQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.quiz.quizView(course.access, course, quiz, None, controllers.quiz.QuestionForms.tangent(errors))),
          form => {
            TangentQuestions.create(TangentQuestionForm.toQuestion(user, form), quizId)
            Redirect(controllers.quiz.routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
          })
      }
    }
  }

  case class TangentDifficultyRequest(functionStr: String, function: String, atPointXStr: String, atPointX: String, partnerSkill: Double)
  case class TangentDifficultyResponse(functionStr: String, function: String, atPointXStr: String, atPointX: String, difficulty: Double, correctPoints: Double, incorrectPoints: Double)
  implicit val formatTangentDifficultyRequest = Json.format[TangentDifficultyRequest]
  implicit val formatTangentDifficultyResponse = Json.format[TangentDifficultyResponse]

  def tangentQuestionDifficulty = Action { request =>
    request.body.asJson.map { configJson =>
      configJson.validate[TangentDifficultyRequest]
        .map { difficultyRequest =>
        (MathML(difficultyRequest.function), MathML(difficultyRequest.atPointX)) match {
          case (Failure(e), _) => BadRequest("Could not parse function [" + difficultyRequest.function + "] as mathml\n" + e.getStackTraceString)
          case (_, Failure(e)) => BadRequest("Could not parse atPointX [" + difficultyRequest.atPointX + "] as mathml\n" + e.getStackTraceString)
          case (Success(function), Success(atPointX)) => {
            val diff = TangentQuestionDifficulty(function)
            val correct = QuestionScoring.teacherScore(diff, true, difficultyRequest.partnerSkill)
            val incorrect = QuestionScoring.teacherScore(diff, false, difficultyRequest.partnerSkill)
            Ok(Json.toJson(TangentDifficultyResponse(difficultyRequest.functionStr, difficultyRequest.function, difficultyRequest.atPointXStr, difficultyRequest.atPointX, diff, correct, incorrect)))
          }
        }
      }.recoverTotal { e => BadRequest("Detected error:" + JsError.toFlatJson(e)) }
    }.getOrElse(BadRequest("Expecting Json data"))
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

  def toQuestion(user: User, form: TangentQuestionForm) = TangentQuestion(null, user.id, form.functionMathML, form.functionStr, form.atPointXMathML, form.atPointXStr, JodaUTC.now, TangentQuestionDifficulty(form.functionMathML))

  def tangentDefined(f: TangentQuestionForm) = {
    val xVal = f.atPointXMathML.evalT().get
    f.functionMathML.isDefinedAt("x" -> xVal) &&
    f.functionMathML.dx.isDefinedAt("x" -> xVal)
  }
}

case class TangentQuestionForm(function: String, functionStr : String, atPointX: String, atPointXStr: String) {
  // TODO handle errors for .get
  def functionMathML = MathML(function).get
  def atPointXMathML = MathML(atPointX).get
}