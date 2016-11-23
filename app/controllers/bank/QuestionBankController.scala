package controllers.bank

import com.artclod.play.CommonsMailerHelper
import com.artclod.slick.JodaUTC
import controllers.quiz.QuestionsController._
import controllers.quiz.derivative.DerivativeQuestionForm
import controllers.quiz.derivativegraph.DerivativeGraphQuestionForm
import controllers.quiz.graphmatch.GraphMatchQuestionForm
import controllers.quiz.multiplechoice.MultipleChoiceQuestionForm
import controllers.quiz.multiplefunction.MultipleFunctionQuestionForm
import controllers.quiz.polynomialzone.PolynomialZoneQuestionForm
import controllers.quiz.tangent.TangentQuestionForm
import controllers.quiz.{QuestionsController, QuizzesController}
import controllers.{FriendRequest, UnfriendRequest}
import controllers.game.GamesEmail._
import controllers.organization.CoursesController._
import models.quiz.question._
import models.user.{Friend, Friends, Users}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import com.artclod.random._
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.support.SecureSocialConsented
import models.organization._
import models.support._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc.{Controller, Result}
import service._
import play.api.data.format.Formats._
import controllers.quiz.QuestionForms
import views.html.helper.options

object QuestionBankController extends Controller with SecureSocialConsented {

  def list = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.bank.list(QuestionForms.empty))
  }


  def delete(questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.bank.list(QuestionForms.empty))
  }


  def view(questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.bank.list(QuestionForms.empty))
//    QuestionsController(questionId) match {
//      case Left(notFoundResult) => notFoundResult
//      case Right((organization, course , quiz, question)) => question match {
//        case derivative :       DerivativeQuestion       => Ok(views.html.bank.question.viewDerivativeQuestion(course, quiz, derivative.results(user), None))
//        case derivativeGraph :  DerivativeGraphQuestion  => Ok(views.html.quiz.derivativegraph.questionView(course, quiz, derivativeGraph.results(user), None))
//        case tangent :          TangentQuestion          => Ok(views.html.quiz.tangent.questionView(course, quiz, tangent.results(user), None))
//        case graphMatch :       GraphMatchQuestion       => Ok(views.html.quiz.graphmatch.questionView(course, quiz, graphMatch.results(user), None))
//        case polyZone :         PolynomialZoneQuestion   => Ok(views.html.quiz.polynomialzone.questionView(course, quiz, polyZone.results(user), None))
//        case multipleChoice :   MultipleChoiceQuestion   => Ok(views.html.quiz.multiplechoice.questionView(course, quiz, multipleChoice.results(user), None))
//        case multipleFunction : MultipleFunctionQuestion => Ok(views.html.quiz.multiplefunction.questionView(course, quiz, multipleFunction.results(user), None))
//      }
//    }
  }

  def createDerivative() = ConsentedAction { implicit request => implicit user => implicit session =>
    DerivativeQuestionForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.bank.list(controllers.quiz.QuestionForms.derivative(errors))),
      form => {
        val question = DerivativeQuestions.create(DerivativeQuestionForm.toQuestion(user, form))
        Redirect(controllers.bank.routes.QuestionBankController.view(question.id))
      })
  }

  def createDerivativeGraph() = ConsentedAction { implicit request => implicit user => implicit session =>
      DerivativeGraphQuestionForm.values.bindFromRequest.fold(
        errors => BadRequest(views.html.bank.list(controllers.quiz.QuestionForms.derivativeGraph(errors))),
        form => {
          val question = DerivativeGraphQuestions.create(DerivativeGraphQuestionForm.toQuestion(user, form))
          Redirect(controllers.bank.routes.QuestionBankController.view(question.id))
        })
  }

  def createTangent() = ConsentedAction { implicit request => implicit user => implicit session =>
    TangentQuestionForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.bank.list(controllers.quiz.QuestionForms.tangent(errors))),
      form => {
        val question = TangentQuestions.create(TangentQuestionForm.toQuestion(user, form))
        Redirect(controllers.bank.routes.QuestionBankController.view(question.id))
      })
  }

  def createGraphMatch() = ConsentedAction { implicit request => implicit user => implicit session =>
    GraphMatchQuestionForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.bank.list(controllers.quiz.QuestionForms.graphMatch(errors))),
      form => {
        val question = GraphMatchQuestions.create(GraphMatchQuestionForm.toQuestion(user, form))
        Redirect(controllers.bank.routes.QuestionBankController.view(question.id))
      })
  }

  def createPolynomialZone() = ConsentedAction { implicit request => implicit user => implicit session =>
    PolynomialZoneQuestionForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.bank.list(controllers.quiz.QuestionForms.polynomialZone(errors))),
      form => {
        val question = PolynomialZoneQuestions.create(PolynomialZoneQuestionForm.toQuestion(user, form))
        Redirect(controllers.bank.routes.QuestionBankController.view(question.id))
      })
  }

  def createMultipleChoice() = ConsentedAction { implicit request => implicit user => implicit session =>
    MultipleChoiceQuestionForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.bank.list(controllers.quiz.QuestionForms.multipleChoice(errors))),
      form => {
        val question = MultipleChoiceQuestions.create(MultipleChoiceQuestionForm.toQuestion(user, form), MultipleChoiceQuestionForm.toOptions(form))
        Redirect(controllers.bank.routes.QuestionBankController.view(question.id))
      })
  }

  def createMultipleFunction() = ConsentedAction { implicit request => implicit user => implicit session =>
    MultipleFunctionQuestionForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.bank.list(controllers.quiz.QuestionForms.multipleFunction(errors))),
      form => {
        val optionsOp = MultipleFunctionQuestionForm.toOptions(form)
        optionsOp match {
          case None => BadRequest(views.html.bank.list(QuestionForms.empty)) // TODO put real error here
          case Some(options) => {
            val question = MultipleFunctionQuestions.create(MultipleFunctionQuestionForm.toQuestion(user, form), options)
            Redirect(controllers.bank.routes.QuestionBankController.view(question.id))
          }
        }
      })
  }

}