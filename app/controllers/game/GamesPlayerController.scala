package controllers.game

import com.artclod.mathml.{Match, Yes, No, Inconclusive}
import com.artclod.mathml.Match._
import com.artclod.mathml.MathML
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.play.CommonsMailerHelper
import com.artclod.slick.JodaUTC
import com.artclod.util._
import controllers.game.GamesController._
import controllers.game.GamesEmail._
import controllers.quiz.QuestionsController
import controllers.quiz.derivative.{DerivativeAnswerForm, DerivativeQuestionForm}
import controllers.quiz.derivativegraph.{DerivativeGraphQuestionForm, DerivativeGraphAnswerForm}
import controllers.quiz.graphmatch.{GraphMatchAnswerForm, GraphMatchQuestionForm}
import controllers.quiz.multiplechoice.{MultipleChoiceQuestionForm, MultipleChoiceAnswerForm}
import controllers.quiz.multiplefunction.{MultipleFunctionQuestionForm, MultipleFunctionAnswerForm }
import controllers.quiz.polynomialzone.{PolynomialZoneAnswerForm, PolynomialZoneQuestionForm}
import controllers.quiz.tangent.{TangentAnswerForm, TangentQuestionForm}
import controllers.support.SecureSocialConsented
import models.game.GameRole._
import models.game._
import models.game.mask.{GameMask, MyStillAnswering, MyQuizFinished, MyQuizUnfinished}
import models.quiz.Quiz
import models.quiz.answer._
import models.quiz.question._
import models.support._
import models.user.{Alerts, User}
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc.{Controller, Result}

import scala.util.{Left, Right}

trait GamesPlayerController extends Controller with SecureSocialConsented {

  // ===== Abstract "To Implement" =====
  protected val playerType: String

  protected def createdQuiz(game: Game)(implicit session: Session): Option[Quiz]

  protected def createdQuizEnsured(game: Game)(implicit user: User, session: Session): (Game, Quiz)

  protected def quizToAnswer(game: Game)(implicit session: Session): Option[Quiz]

  protected def questionToAnswer(gameId: GameId, questionId: QuestionId)(implicit session: Session): Either[Result, (Game, Quiz, Question)]

  // ===== Concrete =====
  protected def finalizeQuizInternal(game: Game)(implicit user: User, session: Session) {
    val gameState = game.toMask(user) match {
      case g: MyQuizUnfinished => g
      case s => throw new IllegalStateException("Mask should have been subclass of " + classOf[MyQuizUnfinished].getName + " but was " + s)
    }
    Games.update(gameState.finalizeMyQuiz)
  }

  protected def finalizeAnswersInternal(game: Game)(implicit user: User, session: Session) {
    val gameState = game.toMask(user) match {
      case g: GameMask with MyQuizFinished with MyStillAnswering => g
      case s => throw new IllegalStateException("Mask should have been subclass of " + classOf[MyQuizFinished].getName + " with " + classOf[MyStillAnswering].getName + " but was " + s)
    }
    val updatedGame = gameState.doneAnswering
    Games.update(updatedGame)
    Alerts.gameAlert(updatedGame.toMask(user))
  }

  protected def questionView(game: Game, quiz: Quiz, question: Question, unfinishedAnswer: Answer)(implicit user: models.user.User, session: Session) : Result = {
    GamesController.questionView(game.toMask(user), quiz, question, Some(Left(unfinishedAnswer)))
  }

  def apply(gameId: GameId, questionId: QuestionId)(implicit session: Session): Either[Result, (Game, Quiz, Question)] =
    Games(gameId) match {
      case None => Left(NotFound(views.html.errors.notFoundPage("There was no game for id=[" + gameId + "]")))
      case Some(game) => createdQuiz(game) match {
        case None => Left(NotFound(views.html.errors.notFoundPage("The game with id=[" + gameId + "] does not have a " + playerType + " Quiz")))
        case Some(quiz) => Right[Result, (Game, Quiz)]((game, quiz)) + QuestionsController(quiz.id, questionId)
      }
    }

  // ===== Add =====
  def addQuestion(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => {
        game.toMask(user) match {
          case mask : mask.MyQuizUnfinished => {
            GameAddQuestion.form.bindFromRequest.fold(
              errors => BadRequest(views.html.errors.formErrorPage(errors)),
              questionIdNum => {
                Questions(QuestionId(questionIdNum)) match {
                  case Some(question) => {
                    val (updatedGame, quiz) = createdQuizEnsured(game)
                    question.attach(quiz.id)
                    Redirect(routes.GamesController.game(updatedGame.id, None))
                  }
                  case None => BadRequest(views.html.errors.errorPage(new IllegalStateException("No question found for ["  + QuestionId(questionIdNum) + "]")))
                }
              }
            )
          }
          case _ =>  BadRequest(views.html.errors.errorPage(new IllegalStateException("Can only add to game quizzes when they are unfinished state ["  + gameId + "]")))
        }
      }
    }
  }

  // ===== Start Create =====
  def createDerivativeQuestion(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        DerivativeQuestionForm.values.bindFromRequest.fold(
          errors =>
            game.toMask(user) match {
              case mask : models.game.mask.MyQuizUnfinished => BadRequest(views.html.game.play.createQuiz(mask, controllers.quiz.QuestionForms.derivative(errors)))
              case _ => BadRequest(views.html.errors.formErrorPage(errors))
            },
          form => {
            val (updatedGame, quiz) = createdQuizEnsured(game)
            DerivativeQuestions.create(DerivativeQuestionForm.toQuestion(user, form), quiz.id)
            Redirect(routes.GamesController.game(updatedGame.id, None))
          })
    }
  }

  def createDerivativeGraphQuestion(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        DerivativeGraphQuestionForm.values.bindFromRequest.fold(
          errors =>
            game.toMask(user) match {
              case mask : models.game.mask.MyQuizUnfinished => BadRequest(views.html.game.play.createQuiz(mask, controllers.quiz.QuestionForms.derivativeGraph(errors)))
              case _ => BadRequest(views.html.errors.formErrorPage(errors))
            },
          form => {
            val (updatedGame, quiz) = createdQuizEnsured(game)
            DerivativeGraphQuestions.create(DerivativeGraphQuestionForm.toQuestion(user, form), quiz.id)
            Redirect(routes.GamesController.game(updatedGame.id, None))
          })
    }
  }

  def createTangentQuestion(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        TangentQuestionForm.values.bindFromRequest.fold(
          errors =>
            game.toMask(user) match {
              case mask : models.game.mask.MyQuizUnfinished => BadRequest(views.html.game.play.createQuiz(mask, controllers.quiz.QuestionForms.tangent(errors)))
              case _ => BadRequest(views.html.errors.formErrorPage(errors))
            },
          form => {
            val (updatedGame, quiz) = createdQuizEnsured(game)
            TangentQuestions.create(TangentQuestionForm.toQuestion(user, form), quiz.id)
            Redirect(routes.GamesController.game(updatedGame.id, None))
          })
    }
  }

  def createGraphMatchQuestion(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        GraphMatchQuestionForm.values.bindFromRequest.fold(
          errors =>
            game.toMask(user) match {
              case mask : models.game.mask.MyQuizUnfinished => BadRequest(views.html.game.play.createQuiz(mask, controllers.quiz.QuestionForms.graphMatch(errors)))
              case _ => BadRequest(views.html.errors.formErrorPage(errors))
            },
          form => {
            val (updatedGame, quiz) = createdQuizEnsured(game)
            GraphMatchQuestions.create(GraphMatchQuestionForm.toQuestion(user, form), quiz.id)
            Redirect(routes.GamesController.game(updatedGame.id, None))
          })
    }
  }

  def createPolynomialZoneQuestion(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        PolynomialZoneQuestionForm.values.bindFromRequest.fold(
          errors =>
            game.toMask(user) match {
              case mask : models.game.mask.MyQuizUnfinished => BadRequest(views.html.game.play.createQuiz(mask, controllers.quiz.QuestionForms.polynomialZone(errors)))
              case _ => BadRequest(views.html.errors.formErrorPage(errors))
            },
          form => {
            val (updatedGame, quiz) = createdQuizEnsured(game)
            PolynomialZoneQuestions.create(PolynomialZoneQuestionForm.toQuestion(user, form), quiz.id)
            Redirect(routes.GamesController.game(updatedGame.id, None))
          })
    }
  }

  def createMultipleChoiceQuestion(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        MultipleChoiceQuestionForm.values.bindFromRequest.fold(
          errors =>
            game.toMask(user) match {
              case mask : models.game.mask.MyQuizUnfinished => BadRequest(views.html.game.play.createQuiz(mask, controllers.quiz.QuestionForms.multipleChoice(errors)))
              case _ => BadRequest(views.html.errors.formErrorPage(errors))
            },
          form => {
            val (updatedGame, quiz) = createdQuizEnsured(game)
            MultipleChoiceQuestions.create(MultipleChoiceQuestionForm.toQuestion(user, form), MultipleChoiceQuestionForm.toOptions(form), quiz.id)
            Redirect(routes.GamesController.game(updatedGame.id, None))
          })
    }
  }

  def createMultipleFunctionQuestion(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        MultipleFunctionQuestionForm.values.bindFromRequest.fold(
          errors =>
            game.toMask(user) match {
              case mask : models.game.mask.MyQuizUnfinished => BadRequest(views.html.game.play.createQuiz(mask, controllers.quiz.QuestionForms.multipleFunction(errors)))
              case _ => BadRequest(views.html.errors.formErrorPage(errors))
            },
          form => {
            val (updatedGame, quiz) = createdQuizEnsured(game)
            MultipleFunctionQuestions.create(MultipleFunctionQuestionForm.toQuestion(user, form), MultipleFunctionQuestionForm.toOptions(form).get, quiz.id)
            Redirect(routes.GamesController.game(updatedGame.id, None))
          })
    }
  }
  // ===== End Create =====

  def removeQuestion(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) =>
        GameRemoveQuestion.form.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          questionId => {
            val (updatedGame, quiz) = createdQuizEnsured(game)
            for (question <- Questions(questionId)) { quiz.remove(question) }
            Redirect(routes.GamesController.game(updatedGame.id, None))
          })
    }
  }

  def finalizeCreatedQuiz(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => {
        finalizeQuizInternal(game)
        for(mail <- game.otherPlayer(user).maybeSendEmail.map(otherMail => CommonsMailerHelper.defaultMailSetup(otherMail))) {
          val userName = user.nameDisplay
          mail.setSubject(userName + " created a CalcTutor game quiz for you")
          mail.sendHtml(userName + " created a game quiz for you in the " + serverLinkEmail(request) + " (" + goToGameLinkEmail(request, game) + ").")
        }
        Redirect(routes.GamesController.game(game.id, None))
      }
    }
  }

  // ===== Start Answer =====
  def answerDerivativeQuestion(gameId: GameId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    questionToAnswer(gameId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((game, quiz, question : DerivativeQuestion)) => {
        DerivativeAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = DerivativeAnswerForm.toAnswerUnfinished(user, question, form)
            DerivativeAnswers.correct(question, form.functionMathML) match {
              case Yes => Redirect(routes.GamesController.game(game.id, Some(DerivativeAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case No => Redirect(routes.GamesController.answer(game.id, question.id, DerivativeAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => questionView(game, quiz, question, unfinishedAnswer(false))
            }
          })
      }
      case Right((game, quiz, _)) => Ok(views.html.errors.notFoundPage("Question was not a derivative question " + questionId))
    }
  }

  def answerDerivativeGraphQuestion(gameId: GameId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    questionToAnswer(gameId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((game, quiz, question: DerivativeGraphQuestion)) => {
        DerivativeGraphAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = DerivativeGraphAnswerForm.toAnswerUnfinished(user, question, form)
            DerivativeGraphAnswers.correct(question, form.derivativeOrder) match {
              case Yes => Redirect(routes.GamesController.game(game.id, Some(DerivativeGraphAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case No => Redirect(routes.GamesController.answer(game.id, question.id, DerivativeGraphAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => questionView(game, quiz, question, unfinishedAnswer(false))
            }
          })
      }
      case Right((game, quiz, _)) => Ok(views.html.errors.notFoundPage("Question was not a derivative graph question " + questionId))
    }
  }

  def answerTangentQuestion(gameId: GameId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    questionToAnswer(gameId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((game, quiz, question: TangentQuestion)) => {
        TangentAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = TangentAnswerForm.toAnswerUnfinished(user, question, form)
            TangentAnswers.correct(question, form.slopeMathML, form.interceptMathML) match {
              case Yes => Redirect(routes.GamesController.game(game.id, Some(TangentAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case No => Redirect(routes.GamesController.answer(game.id, question.id, TangentAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => questionView(game, quiz, question, unfinishedAnswer(false))
            }
          })
      }
      case Right((game, quiz, _)) => Ok(views.html.errors.notFoundPage("Question was not a tangent question " + questionId))
    }
  }

  def answerGraphMatchQuestion(gameId: GameId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    questionToAnswer(gameId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((game, quiz, question: GraphMatchQuestion)) => {
        GraphMatchAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = GraphMatchAnswerForm.toAnswerUnfinished(user, question, form)
            GraphMatchAnswers.correct(question, form.guessIndex) match {
              case Yes => Redirect(routes.GamesController.game(game.id, Some(GraphMatchAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case No => Redirect(routes.GamesController.answer(game.id, question.id, GraphMatchAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => questionView(game, quiz, question, unfinishedAnswer(false))
            }
          })
      }
      case Right((game, quiz, _)) => Ok(views.html.errors.notFoundPage("Question was not a graph match question " + questionId))
    }
  }

  def answerPolynomialZoneQuestion(gameId: GameId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    questionToAnswer(gameId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((game, quiz, question: PolynomialZoneQuestion)) => {
        PolynomialZoneAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = PolynomialZoneAnswerForm.toAnswerUnfinished(user, question, form)
            PolynomialZoneAnswers.correct(question, form.intervals) match {
              case true => Redirect(routes.GamesController.game(game.id, Some(PolynomialZoneAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case false => Redirect(routes.GamesController.answer(game.id, question.id, PolynomialZoneAnswers.createAnswer(unfinishedAnswer(false)).id))
            }
          })
      }
      case Right((game, quiz, _)) => Ok(views.html.errors.notFoundPage("Question was not a polynomial zone question " + questionId))
    }
  }

  def answerMultipleChoiceQuestion(gameId: GameId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    questionToAnswer(gameId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((game, quiz, question: MultipleChoiceQuestion)) => {
        MultipleChoiceAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer =  MultipleChoiceAnswerForm.toAnswerUnfinished(user, question, form)
            MultipleChoiceAnswers.correct(question, form.guessIndex) match {
              case true => Redirect(routes.GamesController.game(game.id, Some(MultipleChoiceAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case false => Redirect(routes.GamesController.answer(game.id, question.id, MultipleChoiceAnswers.createAnswer(unfinishedAnswer(false)).id))
//              case Inconclusive => questionView(game, quiz, question, unfinishedAnswer(false))
            }
          })
      }
      case Right((game, quiz, _)) => Ok(views.html.errors.notFoundPage("Question was not a multiple choice question " + questionId))
    }
  }

  def answerMultipleFunctionQuestion(gameId: GameId, questionId: QuestionId) = ConsentedAction { implicit request => implicit user => implicit session =>
    questionToAnswer(gameId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((game, quiz, question: MultipleFunctionQuestion)) => {
        MultipleFunctionAnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val unfinishedAnswer = MultipleFunctionAnswerForm.toAnswerUnfinished(user, question, form, JodaUTC.now)
            val answerOptions = MultipleFunctionAnswers.answerOptions(question, form)
            Match.from(answerOptions.map(_.correctNum).max) match {
                case Yes => Redirect(routes.GamesController.game(game.id, Some(MultipleFunctionAnswers.createAnswer(unfinishedAnswer(true), answerOptions).id)))
                case No => Redirect(routes.GamesController.answer(game.id, question.id, MultipleFunctionAnswers.createAnswer(unfinishedAnswer(false), answerOptions).id))
                case Inconclusive => questionView(game, quiz, question, unfinishedAnswer(false))
            }
          })
      }
      case Right((game, quiz, _)) => Ok(views.html.errors.notFoundPage("Question was not a multiple choice question " + questionId))
    }
  }
  // ===== End Answer =====

  def finalizeAnswers(gameId: GameId) = ConsentedAction { implicit request => implicit user => implicit session =>
    GamesController(gameId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(game) => {
        finalizeAnswersInternal(game)
        for(mail <- game.otherPlayer(user).maybeSendEmail.map(otherMail => CommonsMailerHelper.defaultMailSetup(otherMail))) {
          val userName = user.nameDisplay
          mail.setSubject(userName + " finished answering your CalcTutor game quiz")
          mail.sendHtml(userName + " finished answering your game quiz in the " + serverLinkEmail(request) + " (" + goToGameLinkEmail(request, game) + ").")
        }
        Redirect(routes.GamesController.game(game.id, None))
      }
    }
  }

}

object GameRemoveQuestion {
  val removeId = "removeId"
  val form = Form(removeId -> questionId)
}
