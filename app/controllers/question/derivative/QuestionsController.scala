package controllers.question.derivative

import scala.slick.session.Session
import mathml.MathML
import models.question._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import securesocial.core.SecureSocial
import service.table._
import service.User
import models.question.derivative._
import models.id._
import models.organization.Courses
import org.joda.time.DateTime

object QuestionsController extends Controller with SecureSocial {

	def create(quizId: QuizId) = SecuredAction { implicit request =>
		implicit val user = User(request)
		QuestionForm.values.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
				val mathML = MathML(form._1).get // TODO better handle on error
				Questions.create(user, QuestionTmp(mathML, form._2, form._3, DateTime.now), quizId)
				Redirect(routes.QuizzesController.view(quizId))
			})
	}

}

object QuestionForm {
	val mathML = "mathML"
	val rawStr = "rawStr"
	val current = "current"
	val values = Form(tuple(mathML -> text, rawStr -> text, current -> boolean))
}
