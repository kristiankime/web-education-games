package controllers.quiz.graphmatch

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

trait GraphMatchQuestionsControllon extends Controller with SecureSocialConsented {

  def createGraphMatch(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        GraphMatchQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.quiz.quizView(course.access, course, quiz, None, controllers.quiz.QuestionForms.graphMatch(errors))),
          form => {
            GraphMatchQuestions.create(GraphMatchQuestionForm.toQuestion(user, form), quizId)
            Redirect(controllers.quiz.routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
          })
      }
    }
  }

  case class GraphMatchDifficultyRequest(functionStr1: String, function1: String, functionStr2: String, function2: String, functionStr3: String, function3: String, graphThis: Short, partnerSkill: Double)
  case class GraphMatchDifficultyResponse(functionStr1: String, function1: String, functionStr2: String, function2: String, functionStr3: String, function3: String, graphThis: Short, difficulty: Double, correctPoints: Double, incorrectPoints: Double)
  implicit val formatGraphMatchDifficultyRequest = Json.format[GraphMatchDifficultyRequest]
  implicit val formatGraphMatchDifficultyResponse = Json.format[GraphMatchDifficultyResponse]

  def graphMatchQuestionDifficulty = Action { request =>
    request.body.asJson.map { configJson =>
      configJson.validate[GraphMatchDifficultyRequest]
        .map { difficultyRequest =>
        (MathML(difficultyRequest.function1), MathML(difficultyRequest.function2), MathML(difficultyRequest.function3)) match {
          case (Failure(e), _, _) => BadRequest("Could not parse function1 [" + difficultyRequest.function1 + "] as mathml\n" + e.getStackTraceString)
          case (_, Failure(e), _) => BadRequest("Could not parse function2 [" + difficultyRequest.function2 + "] as mathml\n" + e.getStackTraceString)
          case (_, _, Failure(e)) => BadRequest("Could not parse function3 [" + difficultyRequest.function3 + "] as mathml\n" + e.getStackTraceString)
          case (Success(function1), Success(function2), Success(function3)) => {
            val diff = GraphMatchQuestionDifficulty(function1, function2, function3, difficultyRequest.graphThis)
            val correct = QuestionScoring.teacherScore(diff, true, difficultyRequest.partnerSkill)
            val incorrect = QuestionScoring.teacherScore(diff, false, difficultyRequest.partnerSkill)
            Ok(Json.toJson(GraphMatchDifficultyResponse(difficultyRequest.functionStr1, difficultyRequest.function1, difficultyRequest.functionStr2, difficultyRequest.function2, difficultyRequest.functionStr3, difficultyRequest.function3, difficultyRequest.graphThis, diff, correct, incorrect)))
          }
        }
      }.recoverTotal { e => BadRequest("Detected error:" + JsError.toFlatJson(e)) }
    }.getOrElse(BadRequest("Expecting Json data"))
  }

}

object GraphMatchQuestionForm {
  // Field Names
  val function1 = "function1"
  val functionStr1 = "functionStr1"
  val function2 = "function2"
  val functionStr2 = "functionStr2"
  val function3 = "function3"
  val functionStr3 = "functionStr3"
  val graphThis = "graphThis"
  // Validation Check Names
  val function1Invalid = "function1Invalid"
  val function2Invalid = "function2Invalid"
  val function3Invalid = "function3Invalid"
  val function1BadDisplay = "function1BadDisplay"
  val function2BadDisplay = "function2BadDisplay"
  val function3BadDisplay = "function3BadDisplay"

  val values = Form(
    mapping(function1 -> nonEmptyText.verifying(f => MathML(f).isSuccess),
            functionStr1 -> nonEmptyText,
            function2 -> nonEmptyText.verifying(f => MathML(f).isSuccess),
            functionStr2 -> nonEmptyText,
            function3 -> nonEmptyText.verifying(f => MathML(f).isSuccess),
            functionStr3 -> nonEmptyText,
            graphThis -> number(min=GraphMatchQuestionWhich.whichMin, max=GraphMatchQuestionWhich.whichMax))
    (GraphMatchQuestionForm.apply)(GraphMatchQuestionForm.unapply)
    verifying(function1Invalid, fields => QuestionForms.verifyFunctionValid(fields.functionMathML1))
    verifying(function2Invalid, fields => QuestionForms.verifyFunctionValid(fields.functionMathML2))
    verifying(function3Invalid, fields => QuestionForms.verifyFunctionValid(fields.functionMathML3))
    verifying(function1BadDisplay, fields => QuestionForms.verifyFunctionDisplaysNicely(fields.functionMathML1))
    verifying(function2BadDisplay, fields => QuestionForms.verifyFunctionDisplaysNicely(fields.functionMathML2))
    verifying(function3BadDisplay, fields => QuestionForms.verifyFunctionDisplaysNicely(fields.functionMathML3))

  )

  def toQuestion(user: User, form: GraphMatchQuestionForm) =
    GraphMatchQuestion(null, user.id, form.functionMathML1, form.functionStr1, form.functionMathML2, form.functionStr2, form.functionMathML3, form.functionStr3, form.graphThis, JodaUTC.now,
      GraphMatchQuestionDifficulty(form.functionMathML2, form.functionMathML2, form.functionMathML3, form.graphThis))

  def fromQuestion(question: GraphMatchQuestion): Form[GraphMatchQuestionForm] = {
    val formFill = GraphMatchQuestionForm(question.function1Math.toString, question.function1Raw, question.function2Math.toString, question.function2Raw, question.function3Math.toString, question.function3Raw, question.graphThis.toInt)
    values.fill(formFill)
  }
}

case class GraphMatchQuestionForm(function1: String, functionStr1: String, function2: String, functionStr2: String, function3: String, functionStr3: String, graphThisInt: Int) {
  // TODO handle errors for .get
  val functionMathML1 = MathML(function1).get
  val functionMathML2 = MathML(function2).get
  val functionMathML3 = MathML(function3).get
  val graphThis = graphThisInt.toShort
}