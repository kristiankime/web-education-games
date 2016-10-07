package controllers.quiz.multiplefunction

import akka.actor.FSM.->
import com.artclod.markup.{MarkupParser, LaikaParser}
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.mathml.{MathMLEq, MathMLRange, Match, MathML}
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
        Redirect(controllers.quiz.routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
      }
    }
  }

//  def createMultipleFunction(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction { implicit request => implicit user => implicit session =>
//    QuizzesController(organizationId, courseId, quizId) match {
//      case Left(notFoundResult) => notFoundResult
//      case Right((organization, course, quiz)) => {
//        null.asInstanceOf[SecuredRequest[AnyContent]]
////        MultipleFunctionQuestionForm.values.bindFromRequest.fold(
////          errors => BadRequest(views.html.quiz.quizView(course.access, course, quiz, None, controllers.quiz.QuestionForms.multipleChoice(errors))),
////          form => {
////            MultipleChoiceQuestions.create( MultipleChoiceQuestionForm.toQuestion(user, form), MultipleChoiceQuestionForm.toOptions(form), quizId)
////            Redirect(controllers.quiz.routes.QuizzesController.view(organization.id, course.id, quiz.id, None))
////          })
//
//      }
//    }
//  }

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
  val explanation = "explanationMF"
  val options = "optionsMF"
  val functions = "functionsMF"
  val functionsStr = "functionsStrMF"
  val difficulty = "difficultyMF"
  // Validation Check Names
  val explanationInvalid = "explanationInvalid"
  val optionsDontMatchFunctions = "optionsDontMatchFunctions"

  val values = Form(
    mapping(explanation -> nonEmptyText.verifying("Explanation could not be parsed as Markup", e => MarkupParser(e).isSuccess),
      options -> list(text).verifying("Options could not be parsed", ops => if(ops.isEmpty){false}else{ops.filter(e => e.trim.nonEmpty).map(e => MarkupParser(e).isSuccess).reduce(_ & _)} ),
      functions -> list(text).verifying("Functions could not be parsed", ops => if(ops.isEmpty){false}else{ops.filter(e => e.trim.nonEmpty).map(e => MathML(e).isSuccess).reduce(_ & _)} ),
      difficulty -> number
    )
    (MultipleFunctionQuestionForm.apply)(MultipleFunctionQuestionForm.unapply)
      verifying(optionsDontMatchFunctions, fields => toOptions(fields).nonEmpty)
  )

//  Option(String, Html, String, MathMLElem)
  def toOptions(form: MultipleFunctionQuestionForm) : Option[List[MultipleFunctionQuestionOption]] = {
    toOptions(form.options, form.functions)
  }

  def toQuestion(user: User, form: MultipleFunctionQuestionForm) = {
    MultipleFunctionQuestion(null, user.id, form.explanation, JodaUTC.now, form.difficulty)
  }

  def toOptions(options: List[String], functions : List[String]) : Option[List[MultipleFunctionQuestionOption]] = {
    if(options.size != functions.size) {
      None;
    } else {
      val opAndFunc = options.map(_.trim).zip(functions.map(_.trim)).filter(o => o._1.isEmpty && o._2.isEmpty)
      if (opAndFunc.map(o => o._1.isEmpty || o._2.isEmpty).fold(false)(_ || _)) {
        None // Blanks mismatched
      } else {
        val parsed = opAndFunc.map(o => (o._1, MarkupParser(o._1), o._2, MathML(o._2)))
        if(parsed.map(o => o._2.isFailure || o._4.isFailure).fold(false)(_ || _)) {
          None // Parsing failures
        } else {
          Some(parsed.map(o => MultipleFunctionQuestionOption(-1, null, o._1, /*o._2.get,*/ o._4.get, o._3)  ) )
        }
      }
    }
  }


//  def toOptions(form: MultipleChoiceQuestionForm) = {
//    val (options, correct) = nonBlankOptionsWithCorrectIndex(form.options, form.correct)
//    options.map( o => MultipleChoiceQuestionOption(-1l, null, o.toString) ).toList
//  }

//  def toQuestion(user: User, form: MultipleFunctionQuestionForm, options: (List[String], Option[Int])) = {
//    MultipleFunctionQuestion(null, user.id, form.explanation, JodaUTC.now, form.difficulty)
//  }

//  def toQuestionOptions(options: (List[String], Option[Int])) =
//    options._1.map( MultipleChoiceQuestionOption(-1, null, _) )

//  // ==== Helpers
//  def nonBlankOptionsWithCorrectBoolean(options : List[String], correct: Int) : List[(String, Boolean)] =
//    options.zipWithIndex.map(o => (o._1, o._2 == correct)).filter(o => o._1.trim.nonEmpty)
//
//  def nonBlankOptionsWithCorrectIndex(options : List[String], correct: Int) : (List[String], Option[Int]) = {
//    val blankOptionsWithCorrectBoolean = nonBlankOptionsWithCorrectBoolean(options, correct)
//    val blankOptionsWithCorrectIndex = nonBlankOptionsWithCorrectIndex(blankOptionsWithCorrectBoolean)
//    blankOptionsWithCorrectIndex
//  }
//
//  def nonBlankOptionsWithCorrectIndex(options: List[(String, Boolean)]) : (List[String], Option[Int]) =
//    ( options.map(o => o._1), options.map(o => o._2).indexOfOp(true) )

}
//case class AnswerOptionData(optionStr: String, option: Html, functionStr: String, function : MathMLElem)

case class MultipleFunctionQuestionForm(explanation: String, options: List[String], functions: List[String], difficultyInt: Int) {
  val difficulty = difficultyInt.toDouble
}