package controllers.quiz.multiplechoice

import akka.actor.FSM.->
import com.artclod.markup.{MarkupParser, LaikaParser}
import com.artclod.mathml.{MathMLEq, MathMLRange, Match, MathML}
import com.artclod.slick.JodaUTC
import controllers.quiz.multiplefunction.MultipleFunctionQuestionForm
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
import play.api.templates.Html
import views.html.{helper, mathml}
import views.html.helper.options
import views.html.mathml.correct
import com.artclod.collection.PimpedGenSeqLike
import play.api.db.slick.Config.driver.simple.Session

trait MultipleChoiceQuestionsControllon extends Controller with SecureSocialConsented {

  def createMultipleChoice(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz)) => {
        MultipleChoiceQuestionForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.quiz.quizView(course.access, course, quiz, None, controllers.quiz.QuestionForms.multipleChoice(errors))),
          form => {
            MultipleChoiceQuestions.create( MultipleChoiceQuestionForm.toQuestion(user, form), MultipleChoiceQuestionForm.toOptions(form), quizId)
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
  val description = "descriptionMC"
  val explanation = "explanationMC"
  val correct = "correctMC"
  val options = "optionsMC"
  val difficulty = "difficultyMC"
  // Validation Check Names
  val explanationInvalid = "explanationInvalid"
  val validCorrectOption = "validCorrectOption"


  val values = Form(
    mapping(
      description -> nonEmptyText,
      explanation -> nonEmptyText.verifying("Explanation could not be parsed as Markup", e => MarkupParser(e).isSuccess),
      correct -> number,
      options -> list(text).verifying("Options could not be parsed", ops => if(ops.isEmpty){false}else{ops.filter(e => e.trim.nonEmpty).map(e => MarkupParser(e).isSuccess).reduce(_ & _)} ),
      difficulty -> number
    )
    (MultipleChoiceQuestionForm.apply)(MultipleChoiceQuestionForm.unapply)
      verifying(validCorrectOption, fields => nonBlankOptionsWithCorrectIndex(fields.options, fields.correct)._2.nonEmpty)
  )

  def toOptions(form: MultipleChoiceQuestionForm) = {
    val (options, correct) = nonBlankOptionsWithCorrectIndex(form.options, form.correct)
    options.map( o => MultipleChoiceQuestionOption(-1l, null, o, MarkupParser(o).getOrElse(Html("Unable to process " + o))) ).toList
  }

  def toQuestion(user: User, form: MultipleChoiceQuestionForm) = {
    MultipleChoiceQuestion(null, user.id, form.description, form.explanation, MarkupParser(form.explanation).getOrElse(Html("Unable to process " + form.explanation)), form.correct, JodaUTC.now, form.difficulty)
  }

  def fromQuestion(question: MultipleChoiceQuestion)(implicit session: Session): Form[MultipleChoiceQuestionForm] = {
    fromQuestion(question, question.answerOptions)
  }

  def fromQuestion(question: MultipleChoiceQuestion, options : List[MultipleChoiceQuestionOption]): Form[MultipleChoiceQuestionForm] = {
    val formObj = MultipleChoiceQuestionForm(question.description, question.explanationRaw, question.correctAnswer, options.map(_.optionRaw), question.difficulty.toInt)
    values.fill(formObj)
  }

  // ==== Helpers
  def nonBlankOptionsWithCorrectBoolean(options : List[String], correct: Int) : List[(String, Boolean)] =
    options.zipWithIndex.map(o => (o._1, o._2 == correct)).filter(o => o._1.trim.nonEmpty)

  def nonBlankOptionsWithCorrectIndex(options : List[String], correct: Int) : (List[String], Option[Int]) = {
    val blankOptionsWithCorrectBoolean = nonBlankOptionsWithCorrectBoolean(options, correct)
    val blankOptionsWithCorrectIndex = nonBlankOptionsWithCorrectIndex(blankOptionsWithCorrectBoolean)
    blankOptionsWithCorrectIndex
  }

  def nonBlankOptionsWithCorrectIndex(options: List[(String, Boolean)]) : (List[String], Option[Int]) =
    ( options.map(o => o._1), options.map(o => o._2).indexOfOp(true) )

}

case class MultipleChoiceQuestionForm(description: String, explanation: String, correctInt: Int, options: List[String], difficultyInt: Int) {
  val correct = correctInt.toShort
  val difficulty = difficultyInt.toDouble
}