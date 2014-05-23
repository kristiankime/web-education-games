package controllers.organization.assignment.quiz

import org.joda.time.DateTime
import com.artclod.mathml.MathML
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import controllers.support.SecureSocialDB
import models.support._
import models.question.derivative._
import models.organization.assignment.Groups

object GroupQuestionsController extends Controller with SecureSocialDB {

	def view(c: CourseId, s: SectionId, a: AssignmentId, g: GroupId, z: QuizId, q: QuestionId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val group = Groups(c, s, a, g)
    val quiz = Quizzes(g, z)
    val question = Questions(z, q)
    Ok(views.html.organization.assignment.quiz.groupQuestionView(group, quiz, question, None))
	}

	def create(c: CourseId, s: SectionId, a: AssignmentId, g: GroupId, z: QuizId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val group = Groups(c, s, a, g)
    val quiz = Quizzes(g, z)

		QuestionForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.errors.formErrorPage(errors)),
			form => {
				val mathML = MathML(form._1).get // TODO better handle on error
				Questions.create(Question(null, user.id, mathML, form._2, DateTime.now), quiz.id)
				Redirect(routes.GroupQuizzesController.view(group.courseId, group.sectionId, group.assignmentId, group.id, quiz.id))
			})
	}

	def remove(c: CourseId, s: SectionId, a: AssignmentId, g: GroupId, z: QuizId, q: QuestionId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    val group = Groups(c, s, a, g)
    val quiz = Quizzes(g, z)
    val question = Questions(z, q)

    quiz.remove(question)
    Redirect(routes.GroupQuizzesController.view(group.courseId, group.sectionId, group.assignmentId, group.id, quiz.id))
	}
}

object QuestionForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val values = Form(tuple(mathML -> text, rawStr -> text))
}
