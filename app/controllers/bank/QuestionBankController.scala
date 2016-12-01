package controllers.bank

import com.artclod.mathml.{Match, Inconclusive, No, Yes}
import com.artclod.play.CommonsMailerHelper
import com.artclod.slick.JodaUTC
import controllers.quiz.AnswersController._
import controllers.quiz.QuestionsController._
import controllers.quiz.derivative.{DerivativeAnswerForm, DerivativeQuestionForm}
import controllers.quiz.derivativegraph.{DerivativeGraphAnswerForm, DerivativeGraphQuestionForm}
import controllers.quiz.graphmatch.{GraphMatchAnswerForm, GraphMatchQuestionForm}
import controllers.quiz.multiplechoice.{MultipleChoiceAnswerForm, MultipleChoiceQuestionForm}
import controllers.quiz.multiplefunction.{MultipleFunctionAnswerForm, MultipleFunctionQuestionForm}
import controllers.quiz.polynomialzone.{PolynomialZoneAnswerForm, PolynomialZoneQuestionForm}
import controllers.quiz.tangent.{TangentAnswerForm, TangentQuestionForm}
import controllers.quiz.{AnswersController, QuestionsController, QuizzesController, QuestionForms}
import controllers.{FriendRequest, UnfriendRequest}
import controllers.game.GamesEmail._
import models.quiz.Quiz
import models.quiz.answer._
import models.quiz.question._
import models.user.{Friend, Friends, Users}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import com.artclod.random._
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.support.{RequireAccess, SecureSocialConsented}
import models.organization._
import models.support._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc.{Controller, Result}
import service._
import play.api.data.format.Formats._
import views.html.bank
import views.html.bank.quiz
import views.html.helper.options
import models.quiz.Quizzes

import scala.util.Right

object QuestionBankController extends Controller with SecureSocialConsented {

  def listQuestions = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.bank.list(QuestionForms.empty))
  }

  // TODO should have access to the question
  def deleteQuestion(questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    Ok(views.html.bank.list(QuestionForms.empty))
  }

  def viewQuestion(questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuestionsController(questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(question) => question match {
        case derivative :       DerivativeQuestion       => Ok(views.html.bank.question.viewDerivativeQuestion(derivative, None))
        case derivativeGraph :  DerivativeGraphQuestion  => Ok(views.html.bank.question.viewDerivativeGraphQuestion(derivativeGraph, None))
        case tangent :          TangentQuestion          => Ok(views.html.bank.question.viewTangentQuestion(tangent, None))
        case graphMatch :       GraphMatchQuestion       => Ok(views.html.bank.question.viewGraphMatchQuestion(graphMatch, None))
        case polyZone :         PolynomialZoneQuestion   => Ok(views.html.bank.question.viewPolynomialZoneQuestion(polyZone, None))
        case multipleChoice :   MultipleChoiceQuestion   => Ok(views.html.bank.question.viewMultipleChoiceQuestion(multipleChoice, None))
        case multipleFunction : MultipleFunctionQuestion => Ok(views.html.bank.question.viewMultipleFunctionQuestion(multipleFunction, None))
      }
    }
  }

  // TODO should have access to the answer
  def viewAnswer(questionId: QuestionId, answerId: AnswerId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuestionsController(questionId) + AnswersController(questionId, answerId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((question, answer)) => (question, answer) match {
        case (dq: DerivativeQuestion, da: DerivativeAnswer)             => Ok(views.html.bank.question.viewDerivativeQuestion(dq, Some(Right(da))))
        case (dq: DerivativeGraphQuestion, da: DerivativeGraphAnswer)   => Ok(views.html.bank.question.viewDerivativeGraphQuestion(dq, Some(Right(da))))
        case (tq: TangentQuestion, ta: TangentAnswer)                   => Ok(views.html.bank.question.viewTangentQuestion(tq, Some(Right(ta))))
        case (tq: GraphMatchQuestion, ta: GraphMatchAnswer)             => Ok(views.html.bank.question.viewGraphMatchQuestion(tq, Some(Right(ta))))
        case (pz: PolynomialZoneQuestion, pa: PolynomialZoneAnswer)     => Ok(views.html.bank.question.viewPolynomialZoneQuestion(pz, Some(Right(pa))))
        case (mc: MultipleChoiceQuestion, ma: MultipleChoiceAnswer)     => Ok(views.html.bank.question.viewMultipleChoiceQuestion(mc, Some(Right(ma))))
        case (mc: MultipleFunctionQuestion, ma: MultipleFunctionAnswer) => Ok(views.html.bank.question.viewMultipleFunctionQuestion(mc, Some(Right(ma))))
        case _ => Ok(views.html.errors.notFoundPage("Question " + questionId + " type did not match Answer " + answerId))
      }
    }
  }

  def viewQuiz(quizId: QuizId) = ConsentedAction(RequireAccess(Edit, quizId)) { implicit request => implicit user => implicit session =>
    QuizzesController(quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(quiz : Quiz) => Ok(views.html.bank.quiz(quiz))
    }
  }

  def setQuizQuestions(quizId: QuizId) = ConsentedAction(RequireAccess(Edit, quizId)) { implicit request => implicit user => implicit session =>
    QuizzesController(quizId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(quiz : Quiz) => {
        BankQuizForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            Quizzes.setQuestions(quiz.id, form.questionIdList.filter(i => i != null && i != -1).map(i => QuestionId(i)))
            Redirect(controllers.bank.routes.QuestionBankController.viewQuiz(quizId))
            })
          }
      }
    }


  // == Derivative
  def createDerivative() = ConsentedAction { implicit request => implicit user => implicit session =>
    DerivativeQuestionForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.bank.list(controllers.quiz.QuestionForms.derivative(errors))),
      form => {
        val question = DerivativeQuestions.create(DerivativeQuestionForm.toQuestion(user, form))
        Redirect(controllers.bank.routes.QuestionBankController.viewQuestion(question.id))
      })
  }

  def answerDerivative(questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuestionsController(questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(question : DerivativeQuestion) => {
        DerivativeAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = DerivativeAnswerForm.toAnswerUnfinished(user, question, form)
            DerivativeAnswers.correct(question, form.functionMathML) match {
              case Yes => Redirect(controllers.bank.routes.QuestionBankController.viewAnswer(question.id, DerivativeAnswers.createAnswer(unfinishedAnswer(true )).id))
              case No =>  Redirect(controllers.bank.routes.QuestionBankController.viewAnswer(question.id, DerivativeAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => Ok(views.html.bank.question.viewDerivativeQuestion(question, Some(Left(unfinishedAnswer(false)))))
            }
          })
      }
      case Right(_) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + DerivativeQuestion.getClass.getSimpleName))
    }
  }

  // == DerivativeGraph
  def createDerivativeGraph() = ConsentedAction { implicit request => implicit user => implicit session =>
      DerivativeGraphQuestionForm.values.bindFromRequest.fold(
        errors => BadRequest(views.html.bank.list(controllers.quiz.QuestionForms.derivativeGraph(errors))),
        form => {
          val question = DerivativeGraphQuestions.create(DerivativeGraphQuestionForm.toQuestion(user, form))
          Redirect(controllers.bank.routes.QuestionBankController.viewQuestion(question.id))
        })
  }

  def answerDerivativeGraph(questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuestionsController(questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(question : DerivativeGraphQuestion) => {
        DerivativeGraphAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = DerivativeGraphAnswerForm.toAnswerUnfinished(user, question, form)
            DerivativeGraphAnswers.correct(question, form.derivativeOrder) match {
              case Yes => Redirect(controllers.bank.routes.QuestionBankController.viewAnswer(question.id, DerivativeGraphAnswers.createAnswer(unfinishedAnswer(true )).id))
              case No =>  Redirect(controllers.bank.routes.QuestionBankController.viewAnswer(question.id, DerivativeGraphAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => Ok(views.html.bank.question.viewDerivativeGraphQuestion(question, Some(Left(unfinishedAnswer(false)))))
            }
          })
      }
      case Right(_) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + DerivativeGraphQuestion.getClass.getSimpleName))
    }
  }

  // == Tangent
  def createTangent() = ConsentedAction { implicit request => implicit user => implicit session =>
    TangentQuestionForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.bank.list(controllers.quiz.QuestionForms.tangent(errors))),
      form => {
        val question = TangentQuestions.create(TangentQuestionForm.toQuestion(user, form))
        Redirect(controllers.bank.routes.QuestionBankController.viewQuestion(question.id))
      })
  }

  def answerTangent(questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuestionsController(questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(question : TangentQuestion) => {
        TangentAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = TangentAnswerForm.toAnswerUnfinished(user, question, form)
            TangentAnswers.correct(question, form.slopeMathML, form.interceptMathML) match {
              case Yes => Redirect(controllers.bank.routes.QuestionBankController.viewAnswer(question.id, TangentAnswers.createAnswer(unfinishedAnswer(true )).id))
              case No =>  Redirect(controllers.bank.routes.QuestionBankController.viewAnswer(question.id, TangentAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => Ok(views.html.bank.question.viewTangentQuestion(question, Some(Left(unfinishedAnswer(false)))))
            }
          })
      }
      case Right(_) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + TangentQuestion.getClass.getSimpleName))
    }
  }

  // == GraphMatch
  def createGraphMatch() = ConsentedAction { implicit request => implicit user => implicit session =>
    GraphMatchQuestionForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.bank.list(controllers.quiz.QuestionForms.graphMatch(errors))),
      form => {
        val question = GraphMatchQuestions.create(GraphMatchQuestionForm.toQuestion(user, form))
        Redirect(controllers.bank.routes.QuestionBankController.viewQuestion(question.id))
      })
  }

  def answerGraphMatch(questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuestionsController(questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(question : GraphMatchQuestion) => {
        GraphMatchAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = GraphMatchAnswerForm.toAnswerUnfinished(user, question, form)
            GraphMatchAnswers.correct(question, form.guessIndex) match {
              case Yes => Redirect(controllers.bank.routes.QuestionBankController.viewAnswer(question.id, GraphMatchAnswers.createAnswer(unfinishedAnswer(true )).id))
              case No =>  Redirect(controllers.bank.routes.QuestionBankController.viewAnswer(question.id, GraphMatchAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => Ok(views.html.bank.question.viewGraphMatchQuestion(question, Some(Left(unfinishedAnswer(false)))))
            }
          })
      }
      case Right(_) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + GraphMatchQuestion.getClass.getSimpleName))
    }
  }

  // == PolynomialZone
  def createPolynomialZone() = ConsentedAction { implicit request => implicit user => implicit session =>
    PolynomialZoneQuestionForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.bank.list(controllers.quiz.QuestionForms.polynomialZone(errors))),
      form => {
        val question = PolynomialZoneQuestions.create(PolynomialZoneQuestionForm.toQuestion(user, form))
        Redirect(controllers.bank.routes.QuestionBankController.viewQuestion(question.id))
      })
  }

  def answerPolynomialZone(questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuestionsController(questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(question : PolynomialZoneQuestion) => {
        PolynomialZoneAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = PolynomialZoneAnswerForm.toAnswerUnfinished(user, question, form)
            PolynomialZoneAnswers.correct(question, form.intervals) match {
              case true  => Redirect(controllers.bank.routes.QuestionBankController.viewAnswer(question.id, PolynomialZoneAnswers.createAnswer(unfinishedAnswer(true )).id))
              case false => Redirect(controllers.bank.routes.QuestionBankController.viewAnswer(question.id, PolynomialZoneAnswers.createAnswer(unfinishedAnswer(false)).id))
            }
          })
      }
      case Right(_) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + MultipleChoiceQuestion.getClass.getSimpleName))
    }
  }

  // == MultipleChoice
  def createMultipleChoice() = ConsentedAction { implicit request => implicit user => implicit session =>
    MultipleChoiceQuestionForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.bank.list(controllers.quiz.QuestionForms.multipleChoice(errors))),
      form => {
        val question = MultipleChoiceQuestions.create(MultipleChoiceQuestionForm.toQuestion(user, form), MultipleChoiceQuestionForm.toOptions(form))
        Redirect(controllers.bank.routes.QuestionBankController.viewQuestion(question.id))
      })
  }

  def answerMultipleChoice(questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuestionsController(questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(question : MultipleChoiceQuestion) => {
        MultipleChoiceAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = MultipleChoiceAnswerForm.toAnswerUnfinished(user, question, form)
            MultipleChoiceAnswers.correct(question, form.guessIndex) match {
              case true  => Redirect(controllers.bank.routes.QuestionBankController.viewAnswer(question.id, MultipleChoiceAnswers.createAnswer(unfinishedAnswer(true )).id))
              case false => Redirect(controllers.bank.routes.QuestionBankController.viewAnswer(question.id, MultipleChoiceAnswers.createAnswer(unfinishedAnswer(false)).id))
            }
          })
      }
      case Right(_) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + MultipleChoiceQuestion.getClass.getSimpleName))
    }
  }

  // == MultipleFunction
  def createMultipleFunction() = ConsentedAction { implicit request => implicit user => implicit session =>
    MultipleFunctionQuestionForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.bank.list(controllers.quiz.QuestionForms.multipleFunction(errors))),
      form => {
        val optionsOp = MultipleFunctionQuestionForm.toOptions(form)
        optionsOp match {
          case None => BadRequest(views.html.bank.list(QuestionForms.empty)) // TODO put real error here
          case Some(options) => {
            val question = MultipleFunctionQuestions.create(MultipleFunctionQuestionForm.toQuestion(user, form), options)
            Redirect(controllers.bank.routes.QuestionBankController.viewQuestion(question.id))
          }
        }
      })
  }

  def answerMultipleFunction(questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    QuestionsController(questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(question : MultipleFunctionQuestion) => {
        MultipleFunctionAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = MultipleFunctionAnswerForm.toAnswerUnfinished(user, question, form, JodaUTC.now)
            val answerOptions = MultipleFunctionAnswers.answerOptions(question, form)
            Match.from(answerOptions.map(_.correctNum).max) match {
              case Yes => Redirect(controllers.bank.routes.QuestionBankController.viewAnswer(question.id, MultipleFunctionAnswers.createAnswer(unfinishedAnswer(true ), answerOptions).id))
              case No =>  Redirect(controllers.bank.routes.QuestionBankController.viewAnswer(question.id, MultipleFunctionAnswers.createAnswer(unfinishedAnswer(false), answerOptions).id))
              case Inconclusive => Ok(views.html.bank.question.viewMultipleFunctionQuestion(question, Some(Left(unfinishedAnswer(false)))))
            }
          })
      }
      case Right(_) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + MultipleFunctionQuestion.getClass.getSimpleName))
    }
  }

}

object BankQuizForm {
  val questionIdList = "questionIdList"

  val values = Form(mapping(questionIdList -> list(number))
  (BankQuizForm.apply)(BankQuizForm.unapply))
}

case class BankQuizForm(questionIdList: List[Int])
