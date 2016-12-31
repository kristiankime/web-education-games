package controllers.quiz.multiplefunction

import akka.actor.FSM.->
import com.artclod.markup.{MarkupParser, LaikaParser}
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.mathml._
import com.artclod.slick.JodaUTC
import controllers.quiz.multiplechoice.MultipleChoiceQuestionForm
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
import play.api.mvc.{AnyContent, Action, Controller}
import play.api.data.format.Formats._
import models.quiz.question.support.DerivativeOrder.derivativeOrderFormatter
import com.artclod.mathml.MathMLEq.tightRange
import play.api.templates.Html
import securesocial.core.SecuredRequest
import views.html.{helper, mathml}
import views.html.helper.{form, options}
import views.html.mathml.correct
import com.artclod.collection.PimpedGenSeqLike
import scala.concurrent.ops
import scala.util.{Success, Failure}

trait MultipleFunctionQuestionsControllon extends Controller with SecureSocialConsented {

  def createMultipleFunction(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        MultipleFunctionQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.quiz.quizView(course.access, course, quiz, None, controllers.quiz.QuestionForms.multipleFunction(errors))),
          form => {
            MultipleFunctionQuestions.create( MultipleFunctionQuestionForm.toQuestion(user, form), MultipleFunctionQuestionForm.toOptions(form).get, quizId)
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

object MultipleFunctionQuestionForm {
  // Field Names
  val description = "descriptionMF"
  val explanation = "explanationMF"
  val options = "optionsMF"
  val functions = "functionsMF"
  val functionsStr = "functionsStrMF"
  val difficulty = "difficultyMF"
  // Validation Check Names
  val explanationInvalid = "explanationInvalid"
  val optionsDontMatchFunctions = "optionsDontMatchFunctions"

  val values = Form(
    mapping(
      description -> nonEmptyText,
      explanation -> nonEmptyText.verifying("Explanation could not be parsed as Markup", e => MarkupParser(e).isSuccess),
      options -> list(text).verifying("Options could not be parsed", ops => if(ops.filter(e => e.trim.nonEmpty).isEmpty){false}else{ops.filter(e => e.trim.nonEmpty).map(e => MarkupParser(e).isSuccess).reduce(_ & _)}),
      functions -> list(text).verifying("Functions could not be parsed", ops => if(ops.filter(e => e.trim.nonEmpty).isEmpty){false}else{ops.filter(e => e.trim.nonEmpty).map(e => MathML(e).isSuccess).reduce(_ & _)}),
      functionsStr -> list(text),
      difficulty -> number
    )
    (MultipleFunctionQuestionForm.apply)(MultipleFunctionQuestionForm.unapply)
      verifying(optionsDontMatchFunctions, fields => toOptions(fields).nonEmpty)
  )

  def fromQuestion(question: MultipleFunctionQuestion) : Form[controllers.quiz.multiplefunction.MultipleFunctionQuestionForm] = {
    val foo = MultipleFunctionQuestionForm(question.description, question.explanationRaw, null, null, null, question.difficulty.toInt)
    values.fill(foo)
  }

  def toQuestion(user: User, form: MultipleFunctionQuestionForm) = {
    MultipleFunctionQuestion(null, user.id, form.description, form.explanation, MarkupParser(form.explanation).getOrElse(Html("Unable to process " + form.explanation)), JodaUTC.now, form.difficulty)
  }

  def toOptions(form: MultipleFunctionQuestionForm) : Option[List[MultipleFunctionQuestionOption]] = {
    toOptions(form.options, form.functions, form.functionsStr)
  }

  def toOptions(optionsIn: List[String], functionsIn : List[String], functionsStrsIn : List[String]) : Option[List[MultipleFunctionQuestionOption]] = {
    // Must have option text for anything to make sense
    val size = optionsIn.size;
    // Create lists where each has option value (some if valid value, non otherwise)
    def isValid(s: String): Boolean = { (s != null) && (s.nonEmpty) }
    def isValid2Some(s: String): Option[String] = if (isValid(s)) { Some(s) } else { None }
    val options      =        optionsIn.map(_.trim)                .map(isValid2Some(_)).map( _.flatMap(o => MarkupParser(o).toOption))
    val optionsStrs  =        optionsIn.map(_.trim)                .map(isValid2Some(_))
    val functions    =      functionsIn.map(_.trim).padTo(size, "").map(isValid2Some(_)).map( _.flatMap(o => MathML(o).toOption))
    val functionStrs =  functionsStrsIn.map(_.trim).padTo(size, "").map(isValid2Some(_))

    // Check to see if any row set that has options has invalid functions
    var error = false
    for(i <- 0 until size if options(i).isDefined) {
      if(functions(i).isEmpty | functionStrs(i).isEmpty) { error = true }
    }

    if(error) {
      None
    } else { Some(
      (for(i <- 0 until size if options(i).isDefined if functions(i).isDefined if functionStrs(i).isDefined) yield {
        val option = options(i)
        val optionStr = optionsStrs(i)
        val function = functions(i)
        val functionStr = functionStrs(i)

        MultipleFunctionQuestionOption(-1, null, optionStr.get, option.get, function.get, functionStr.get)
      }).toList
    )}
  }

}

case class MultipleFunctionQuestionForm(description: String, explanation: String, options: List[String], functions: List[String], functionsStr: List[String], difficultyInt: Int) {
  val difficulty = difficultyInt.toDouble
}