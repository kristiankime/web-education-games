package controllers.quiz.derivative

import com.artclod.mathml.Match._
import com.artclod.mathml.scalar.MathMLElem
import controllers.quiz.{AnswerForm, QuestionsController, QuizzesController}
import models.quiz.answer.{DerivativeAnswers, DerivativeAnswerUnfinished}
import models.quiz.{Quizzes, Quiz}
import models.quiz.question.{QuestionDifficulty, DerivativeQuestion, DerivativeQuestions}
import com.artclod.slick.JodaUTC
import com.artclod.mathml.MathML
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Controller}
import controllers.support.{SecureSocialConsented}
import models.support._
import com.artclod.util._

trait DerivativeAnswersControllon extends Controller with SecureSocialConsented {

  def createDerivative(organizationId: OrganizationId, courseId: CourseId, quizId: QuizId, questionId: QuestionId) = ConsentedAction("TODO REMOVE ME WHEN INTELLIJ 14 CAN PARSE WITHOUT THIS") { implicit request => implicit user => implicit session =>
    QuizzesController(organizationId, courseId, quizId) + QuestionsController(quizId, questionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right((organization, course, quiz, question : DerivativeQuestion)) => {
        AnswerForm.values.bindFromRequest.fold(
          errors => BadRequest(views.html.errors.formErrorPage(errors)),
          form => {
            val math : MathMLElem = MathML(form._1).get // TODO better error handling
            val rawStr = form._2
            val quizOp : Option[Quiz] = Quizzes(quizId)
            val unfinishedAnswer = DerivativeAnswerUnfinished(user.id, question.id, math, rawStr, JodaUTC.now)_
            DerivativeAnswers.correct(question, math) match {
              case Yes => Redirect(controllers.quiz.routes.QuizzesController.view(course.organizationId, course.id, quiz.id, Some(DerivativeAnswers.createAnswer(unfinishedAnswer(true)).id)))
              case No => Redirect(controllers.quiz.routes.AnswersController.view(course.organizationId, course.id, quiz.id, question.id, DerivativeAnswers.createAnswer(unfinishedAnswer(false)).id))
              case Inconclusive => Ok(views.html.quiz.derivative.questionView(course, quiz, question.results(user), Some(Left(unfinishedAnswer(false)))))
            }
          })
      }
      case Right((organization, course, quiz, _)) => Ok(views.html.errors.notFoundPage("Question " + questionId + " was not a " + DerivativeQuestion.getClass.getSimpleName))
    }
  }

}

object DerivativeQuestionForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> nonEmptyText, rawStr -> nonEmptyText))
}
