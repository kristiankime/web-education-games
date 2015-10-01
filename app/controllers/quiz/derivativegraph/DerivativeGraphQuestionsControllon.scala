package controllers.quiz.derivativegraph

import com.artclod.mathml.{MathMLEq, MathMLRange, Match, MathML}
import com.artclod.slick.JodaUTC
import controllers.quiz.{QuestionForms, QuizzesController}
import controllers.quiz.derivative.DerivativeQuestionForm
import controllers.quiz.tangent.{TangentQuestionForm, TangentAnswerForm}
import controllers.support.SecureSocialConsented
import models.quiz.question._
import models.quiz.question.support.DerivativeOrder
import models.support._
import models.user.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}
import play.api.data.format.Formats._
import models.quiz.question.support.DerivativeOrder.derivativeOrderFormatter
import com.artclod.mathml.MathMLEq.tightRange

import scala.util.{Success, Failure}

trait DerivativeGraphQuestionsControllon extends Controller with SecureSocialConsented {

  def createDerivativeGraph(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        DerivativeGraphQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.quiz.quizView(course.access, course, quiz, None, controllers.quiz.QuestionForms.derivativeGraph(errors))),
          form => {
            DerivativeGraphQuestions.create(DerivativeGraphQuestionForm.toQuestion(user, form), quizId)
            Redirect(controllers.quiz.routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
          })
      }
    }
  }

  case class DerivativeGraphDifficultyRequest(functionStr: String, function: String, derivativeOrder: String, showFunction: Boolean, partnerSkill: Double)
  case class DerivativeGraphDifficultyResponse(functionStr: String, function: String, derivativeOrder: String, showFunction: Boolean, difficulty: Double, correctPoints: Double, incorrectPoints: Double)
  implicit val formatDerivativeGraphDifficultyRequest = Json.format[DerivativeGraphDifficultyRequest]
  implicit val formatDerivativeGraphDifficultyResponse = Json.format[DerivativeGraphDifficultyResponse]

  def derivativeGraphQuestionDifficulty = Action { request =>
    request.body.asJson.map { configJson =>
      configJson.validate[DerivativeGraphDifficultyRequest]
        .map { difficultyRequest =>
        (MathML(difficultyRequest.function)) match {
          case (Failure(e)) => BadRequest("Could not parse function [" + difficultyRequest.function + "] as mathml\n" + e.getStackTraceString)
          case (Success(function)) => {
            val diff = DerivativeGraphQuestionDifficulty(function, difficultyRequest.showFunction)
            val correct = QuestionScoring.teacherScore(diff, true, difficultyRequest.partnerSkill)
            val incorrect = QuestionScoring.teacherScore(diff, false, difficultyRequest.partnerSkill)
            Ok(Json.toJson(DerivativeGraphDifficultyResponse(difficultyRequest.functionStr, difficultyRequest.function, difficultyRequest.derivativeOrder, difficultyRequest.showFunction, diff, correct, incorrect)))
          }
        }
      }.recoverTotal { e => BadRequest("Detected error:" + JsError.toFlatJson(e)) }
    }.getOrElse(BadRequest("Expecting Json data"))
  }

}

object DerivativeGraphQuestionForm {
  // Field Names
  val function = "function"
  val functionStr = "functionStr"
  val derivativeOrder = "derivativeOrder"
  val showFunction = "showFunction"
  // Validation Check Names
  val rangeValid = "rangeValid"
  val functionInvalid = "functionInvalid"
  val functionsSame = "functionsSame"
  val functionsDisplayNicely = "functionsDisplayNicely"

  val values = Form(
    mapping(function -> nonEmptyText.verifying(f => MathML(f).isSuccess),
            functionStr -> nonEmptyText,
            derivativeOrder -> of[DerivativeOrder],
            showFunction -> boolean)
    (DerivativeGraphQuestionForm.apply)(DerivativeGraphQuestionForm.unapply)
    verifying(rangeValid, fields => verifyRangeValid(fields) )
    verifying(functionInvalid, fields => QuestionForms.verifyFunctionValid(fields.functionMathML))
    verifying(functionsSame, fields => verifyFunctionsDifferent(fields))
    verifying(functionsDisplayNicely, fields => QuestionForms.verifyFunctionDisplaysNicely(fields.functionMathML))
  )

  def toQuestion(user: User, form: DerivativeGraphQuestionForm) = DerivativeGraphQuestion(null, user.id, form.functionMathML, form.functionStr, form.showFunction, JodaUTC.now, form.derivativeOrder, DerivativeGraphQuestionDifficulty(form.functionMathML, form.showFunction))

  private def verifyRangeValid(f: DerivativeGraphQuestionForm) = f.rangeLow < f.rangeHigh

  private def verifyFunctionsDifferent(form: DerivativeGraphQuestionForm) = {
    val f = form.functionMathML
    val fp = f.dx
    val fpp = fp.dx

    if(      (f  ?= fp)  == Match.Yes ) { false }
    else if( (f  ?= fpp) == Match.Yes ) { false }
    else if( (fp ?= fpp) == Match.Yes ) { false }
    else                                { true  }
  }

}

case class DerivativeGraphQuestionForm(function: String, functionStr : String, derivativeOrder: DerivativeOrder, showFunction: Boolean) {
  val rangeLow = -1d * tightRange
  val rangeHigh = tightRange
  // TODO handle errors for .get
  def functionMathML = MathML(function).get
}