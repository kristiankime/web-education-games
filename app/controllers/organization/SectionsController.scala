package controllers.organization

import play.api.db.slick.Config.driver.simple.Session
import scala.util.Random
import com.artclod.slick.Joda
import com.artclod.random._
import com.artclod.util._
import play.api.mvc.{Result, Controller}
import play.api.data.Form
import play.api.data.Forms._
import models.organization._
import models.support._
import service._
import controllers.support.SecureSocialDB
import controllers.support.RequireAccess

object SectionsController extends Controller with SecureSocialDB {
	implicit val randomEngine = new Random(Joda.now.getMillis())
  val codeRange = (0 to 100000).toVector

  def apply(courseId: CourseId, sectionId: SectionId)(implicit session: Session) : Either[Result, Section] =
  Sections(sectionId) match {
    case None => Left(NotFound(views.html.errors.notFoundPage("There was no section for id=["+sectionId+"]")))
    case Some(section) => {
      if(section.courseId !== courseId) Left(NotFound(views.html.errors.notFoundPage("courseId=[" + courseId +"] was not for sectionId=["+sectionId+"]")))
      else Right(section)
    }
  }
  
	def add(courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId))  { implicit request => implicit user => implicit session =>
    CoursesController(courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(course) => Ok(views.html.organization.sectionAdd(course))
    }
	}

	def create(courseId: CourseId) = SecuredUserDBAction(RequireAccess(Edit, courseId)) { implicit request => implicit user => implicit session =>
    CoursesController(courseId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(course) =>
        SectionCreate.form.bindFromRequest.fold(
          errors => BadRequest(views.html.index()),
          form => {
            val (editNum, viewNum) = codeRange.pick2From
            val editCode = "SE-E-" + editNum
            val viewCode = "SE-V-" + viewNum
            val now = Joda.now
            Sections.create(Section(null, form, courseId, user.id, editCode, viewCode, now, now))
            Redirect(routes.CoursesController.view(courseId))
          })
    }
	}

	def view(courseId: CourseId, sectionId: SectionId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    SectionsController(courseId, sectionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(section) => Ok(views.html.organization.sectionView(section))
    }
	}

	def join(courseId: CourseId, sectionId: SectionId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    SectionsController(courseId, sectionId) match {
      case Left(notFoundResult) => notFoundResult
      case Right(section) =>
        SectionJoin.form.bindFromRequest.fold(
          errors => BadRequest(views.html.index()),
          form => {
              if (section.viewCode == form)	Sections.grantAccess(section, View)
              if (section.editCode == form) Sections.grantAccess(section, Edit)
              Redirect(routes.SectionsController.view(section.courseId, sectionId)) // TODO indicate access was not granted in a better fashion
          })
    }
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
