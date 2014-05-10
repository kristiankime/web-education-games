package controllers.organization

import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import models.organization._
import models.support._
import org.joda.time.DateTime
import service._
import scala.util.Random
import controllers.support.SecureSocialDB
import controllers.support.RequireAccess
import com.artclod.random._

object SectionsController extends Controller with SecureSocialDB {
	implicit val randomEngine = new Random(DateTime.now.getMillis())
  val codeRange = (0 to 100000).toVector

	def add(courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId))  { implicit request => implicit user => implicit session =>
		Courses(courseId) match {
			case Some(course) => Ok(views.html.organization.sectionAdd(course))
			case None => NotFound(views.html.index())
		}
	}

	def create(courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
		SectionCreate.form.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => {
        val (editNum, viewNum) = codeRange.pick2From
        val editCode = "SE-E-" + editNum
				val viewCode = "SE-V-" + viewNum
				Sections.create(SectionTmp(form, courseId, user.id, editCode, viewCode, DateTime.now))
				Redirect(routes.CoursesController.view(courseId))
			})
	}

	def view(courseId: CourseId, id: SectionId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    Sections(id) match {
      case None => NotFound(views.html.index())
      case Some(section) =>
        if (section.courseId != courseId) Redirect(routes.SectionsController.view(section.courseId, id))
        else Ok(views.html.organization.sectionView(section.details, section.quizzes, section.assignments))
    }
	}

	def join(courseId: CourseId, id: SectionId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
		SectionJoin.form.bindFromRequest.fold(
			errors => BadRequest(views.html.index()),
			form => Sections(id) match {
					    case Some(section) => {
                if (section.viewCode == form)	Sections.grantAccess(section, View)
                if (section.editCode == form) Sections.grantAccess(section, Edit)
                Redirect(routes.SectionsController.view(section.courseId, id)) // TODO indicate acesss was not granted in a better fashion
              }
              case None => BadRequest(views.html.index())
          })
	}

}

object SectionCreate {
	val name = "name"
	val form = Form(name -> nonEmptyText)
}

object SectionJoin {
	val code = "code"
	val form = Form(code -> text)
}
