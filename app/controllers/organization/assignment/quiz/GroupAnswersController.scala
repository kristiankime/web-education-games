package controllers.organization.assignment.quiz

import scala.util._
import org.joda.time.DateTime
import com.artclod.mathml.MathML
import com.artclod.mathml.Match._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import controllers.support.SecureSocialDB
import models.support._
import models.organization._
import models.organization.assignment._
import models.question.derivative._
import service._
import play.api.db.slick.Config.driver.simple._


object GroupAnswersController extends Controller with SecureSocialDB {

	def view(c: CourseId, s: SectionId, a: AssignmentId, g: GroupId, z: QuizId, q: QuestionId, n: AnswerId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val group = Groups(c, s, a, g)
    val quiz = Quizzes(g, z)
    val question = Questions(z, q)
    val answer = Answers(q, n).get // TODO better error message here

    Ok(views.html.organization.assignment.quiz.groupQuestionView(group, quiz, question, Some(Right(answer))))
	}

	def create(c: CourseId, s: SectionId, a: AssignmentId, g: GroupId, z: QuizId, q: QuestionId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val group = Groups(c, s, a, g)
    val quiz = Quizzes(g, z)
    val question = Questions(z, q)

    AnswerForm.values.bindFromRequest.fold(
      errors => BadRequest(views.html.errors.formErrorPage(errors)),
      form => {
        val math = MathML(form._1).get // TODO better error here
        val rawStr = form._2
        val aTmp = AnswerLater(user.id, question.id, math, rawStr, DateTime.now)_

        Answers.correct(question, math) match {
          case Yes => Redirect(routes.GroupAnswersController.view(c, s, a, g, quiz.id, question.id, Answers.createAnswer(aTmp(true)).id))
          case No => Redirect(routes.GroupAnswersController.view(c, s, a, g, quiz.id, question.id, Answers.createAnswer(aTmp(false)).id))
          case Inconclusive => Ok(views.html.organization.assignment.quiz.groupQuestionView(group, quiz, question, Some(Left(aTmp(false)))))
        }
      })
	}

}

object AnswerForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> text, rawStr -> text))
}
