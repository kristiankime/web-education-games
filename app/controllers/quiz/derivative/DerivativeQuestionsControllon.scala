package controllers.quiz.derivative

import com.artclod.mathml.{MathMLDefined, MathML}
import com.artclod.slick.JodaUTC
import controllers.quiz.{QuestionForms, QuizzesController}
import controllers.support.SecureSocialConsented
import models.quiz.question._
import models.support._
import models.user.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

import scala.util.{Success, Failure, Left}

trait DerivativeQuestionsControllon extends Controller with SecureSocialConsented {

  def createDerivative(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        DerivativeQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.quiz.quizView(course.access, course, quiz, None, controllers.quiz.QuestionForms.derivative(errors))),
          form => {
            DerivativeQuestions.create(DerivativeQuestionForm.toQuestion(user, form), quiz.id)
            Redirect(controllers.quiz.routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
          })
      }
    }
  }

  case class DerivativeDifficultyRequest(functionStr: String, function: String, partnerSkill: Double)
  case class DerivativeDifficultyResponse(functionStr: String, function: String, difficulty: Double, correctPoints: Double, incorrectPoints: Double)
  implicit val formatDerivativeDifficultyRequest = Json.format[DerivativeDifficultyRequest]
  implicit val formatDerivativeDifficultyResponse = Json.format[DerivativeDifficultyResponse]

  def derivativeQuestionDifficulty = Action { request =>
    request.body.asJson.map { configJson =>
      configJson.validate[DerivativeDifficultyRequest]
        .map { difficultyRequest =>
        MathML(difficultyRequest.function) match {
          case Failure(e) => BadRequest("Could not parse [" + difficultyRequest.function + "] as mathml\n" + e.getStackTraceString)
          case Success(mathML) => {
            val diff = DerivativeQuestionDifficulty(mathML)
            val correct = QuestionScoring.teacherScore(diff, true, difficultyRequest.partnerSkill)
            val incorrect = QuestionScoring.teacherScore(diff, false, difficultyRequest.partnerSkill)
            Ok(Json.toJson(DerivativeDifficultyResponse(difficultyRequest.functionStr, difficultyRequest.function, diff, correct, incorrect)))
          }
        }
      }.recoverTotal { e => BadRequest("Detected error:" + JsError.toFlatJson(e)) }
    }.getOrElse(BadRequest("Expecting Json data"))
  }

}

object DerivativeQuestionForm {
  val function = "function"
  val functionStr = "functionStr"
  // Validation Check Names
  val functionInvalid = "functionInvalid"
  val functionDerivativeIsNotEasyToType = "functionDerivativeIsNotEasyToType"

  val values = Form(
    mapping(function -> nonEmptyText.verifying(f => MathML(f).isSuccess),
            functionStr -> nonEmptyText)
    (DerivativeQuestionForm.apply)(DerivativeQuestionForm.unapply)
    verifying(functionInvalid, fields => QuestionForms.verifyFunctionValid(fields.functionMathML))
    verifying(functionDerivativeIsNotEasyToType, fields => QuestionForms.verifyFunctionDerivativeIsEasyToType(fields.functionMathML))
  )

  def toQuestion(user: User, form: DerivativeQuestionForm) = DerivativeQuestion(null, user.id, form.functionMathML, form.functionStr, JodaUTC.now, DerivativeQuestionDifficulty(form.functionMathML))
}

case class DerivativeQuestionForm(function: String, functionStr: String) {
  def functionMathML = MathML(function).get // TODO better handle on error for .get
}