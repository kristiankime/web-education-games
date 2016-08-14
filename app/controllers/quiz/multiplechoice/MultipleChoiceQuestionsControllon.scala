package controllers.quiz.multiplechoice

import akka.actor.FSM.->
import com.artclod.mathml.{MathMLEq, MathMLRange, Match, MathML}
import com.artclod.slick.JodaUTC
import com.artclod.util.LaikaParser
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
import views.html.{helper, mathml}
import views.html.helper.options
import views.html.mathml.correct

import scala.concurrent.ops
import scala.util.{Success, Failure}

trait MultipleChoiceQuestionsControllon extends Controller with SecureSocialConsented {

  def createMultipleChoice(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        MultipleChoiceQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.quiz.quizView(course.access, course, quiz, None, controllers.quiz.QuestionForms.multipleChoice(errors))),
          form => {
            MultipleChoiceQuestions.create(MultipleChoiceQuestionForm.toQuestion(user, form), quizId)
            Redirect(controllers.quiz.routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
          })
      }
    }
  }

//  case class MultipleChoiceDifficultyRequest(functionStr1: String, function1: String, functionStr2: String, function2: String, functionStr3: String, function3: String, graphThis: Short, partnerSkill: Double)
//  case class MultipleChoiceDifficultyResponse(functionStr1: String, function1: String, functionStr2: String, function2: String, functionStr3: String, function3: String, graphThis: Short, difficulty: Double, correctPoints: Double, incorrectPoints: Double)
//  implicit val formatMultipleChoiceDifficultyRequest = Json.format[MultipleChoiceDifficultyRequest]
//  implicit val formatMultipleChoiceDifficultyResponse = Json.format[MultipleChoiceDifficultyResponse]
//
//  def multipleChoiceQuestionDifficulty = Action { request =>
//    request.body.asJson.map { configJson =>
//      configJson.validate[MultipleChoiceDifficultyRequest]
//        .map { difficultyRequest =>
//        (MathML(difficultyRequest.function1), MathML(difficultyRequest.function2), MathML(difficultyRequest.function3)) match {
//          case (Failure(e), _, _) => BadRequest("Could not parse function1 [" + difficultyRequest.function1 + "] as mathml\n" + e.getStackTraceString)
//          case (_, Failure(e), _) => BadRequest("Could not parse function2 [" + difficultyRequest.function2 + "] as mathml\n" + e.getStackTraceString)
//          case (_, _, Failure(e)) => BadRequest("Could not parse function3 [" + difficultyRequest.function3 + "] as mathml\n" + e.getStackTraceString)
//          case (Success(function1), Success(function2), Success(function3)) => {
//            val diff = MultipleChoiceQuestionDifficulty(function1, function2, function3, difficultyRequest.graphThis)
//            val correct = QuestionScoring.teacherScore(diff, true, difficultyRequest.partnerSkill)
//            val incorrect = QuestionScoring.teacherScore(diff, false, difficultyRequest.partnerSkill)
//            Ok(Json.toJson(MultipleChoiceDifficultyResponse(difficultyRequest.functionStr1, difficultyRequest.function1, difficultyRequest.functionStr2, difficultyRequest.function2, difficultyRequest.functionStr3, difficultyRequest.function3, difficultyRequest.graphThis, diff, correct, incorrect)))
//          }
//        }
//      }.recoverTotal { e => BadRequest("Detected error:" + JsError.toFlatJson(e)) }
//    }.getOrElse(BadRequest("Expecting Json data"))
//  }

}

object MultipleChoiceQuestionForm {
  // Field Names
  val explanation = "explanation"
  val correct = "correct"
  val options = "options"
  val difficulty = "difficulty"
  // Validation Check Names
  val explanationInvalid = "explanationInvalid"

  val values = Form(
    mapping(explanation -> nonEmptyText.verifying("Explanation could not be parsed as Markup", e => LaikaParser(e).isSuccess),
      correct -> number,
      options -> list(nonEmptyText).verifying("Options could not be parsed", ops => if(ops.isEmpty){false}else{ops.map(e => LaikaParser(e).isSuccess).reduce(_ & _)} ),
      difficulty -> number
    )
    (MultipleChoiceQuestionForm.apply)(MultipleChoiceQuestionForm.unapply)
  )

  def toQuestion(user: User, form: MultipleChoiceQuestionForm) =
    MultipleChoiceQuestion(null, user.id, form.explanation, form.correct, JodaUTC.now, form.difficulty)

}

case class MultipleChoiceQuestionForm(explanation: String, correctInt: Int, options: List[String], difficultyInt: Int) {
  val correct = correctInt.toShort
  val difficulty = difficultyInt.toDouble
}