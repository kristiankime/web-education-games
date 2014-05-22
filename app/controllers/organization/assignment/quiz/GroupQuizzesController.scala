package controllers.organization.assignment.quiz

import com.artclod.util._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.Session
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import service._
import models.support._
import models.question.derivative._
import models.organization.Courses
import models.organization.assignment._
import controllers.support.SecureSocialDB

object GroupQuizzesController extends Controller with SecureSocialDB {

  private def groupCheck(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId)(implicit user: User, session: Session) : Option[Either[Group, Group]] = Groups(groupId) match {
    case None => None
    case Some(group) =>
      if (group.idsMatch(courseId, sectionId, assignmentId)) Some(Right(group))
      else Some(Left(group))
  }

  def add(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
    groupCheck(courseId, sectionId, assignmentId, groupId) match {
      case None => BadRequest(views.html.errors.notFoundPage("Could not find the group with id " + groupId))
      case Some(Left(group)) => Redirect(routes.GroupQuizzesController.add(group.courseId, group.sectionId, group.assignmentId, groupId))
      case Some(Right(group)) =>Ok(views.html.organization.assignment.quiz.groupQuizAdd(group))
    }
  }

  def create(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
//    val where = eitherOp(courseIdOp.flatMap(Courses(_)), groupIdOp.flatMap(Groups(_)))
//
//    QuizForm.values.bindFromRequest.fold(
//      errors => {
//        Logger("create").info("error" + errors)
//        BadRequest(views.html.errors.formErrorPage(errors))
//      },
//      form => where match {
//        case Right(group) => {
//          val now = DateTime.now
//          val quiz = Quizzes.create(Quiz(null, user.id, form, now, now), group.id)
//          Redirect(routes.GroupQuizzesController.view(quiz.id, where.leftOp(_.id), where.rightOp(_.id)))
//        }
//        case Left(course) => {
//          val now = DateTime.now
//          val quiz = Quizzes.create(Quiz(null, user.id, form, now, now), course.id)
//          Redirect(routes.GroupQuizzesController.view(quiz.id, where.leftOp(_.id), where.rightOp(_.id)))
//        }
//      })
    ???
  }

  def view(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
//    val where = eitherOp(courseIdOp.flatMap(Courses(_)), groupIdOp.flatMap(Groups(_)))
//    val course = models.organization.courseFrom(where)
//    val access = where.access
//
//    Quizzes(quizId) match {
//      case Some(quiz) => {
//        val results = access.write(() => course.sectionResults(quiz))
//        Ok(views.html.question.derivative.quizView(access, where, quiz.results(user), results))
//      }
//      case _ => BadRequest(views.html.index())
//    }
    ???
  }

  def rename(courseId: CourseId, sectionId: SectionId, assignmentId: AssignmentId, groupId: GroupId, quizId: QuizId) = SecuredUserDBAction { implicit request => implicit user => implicit session =>
//    val where = eitherOp(courseIdOp.flatMap(Courses(_)), groupIdOp.flatMap(Groups(_)))
//
//    QuizForm.values.bindFromRequest.fold(
//      errors => BadRequest(views.html.index()),
//      form => {
//        Quizzes.rename(quizId, form)
//        Redirect(routes.GroupQuizzesController.view(quizId, where.leftOp(_.id), where.rightOp(_.id)))
//      })
    ???
  }

}

object QuizForm {
  val name = "name"

  val values = Form(name -> nonEmptyText)
}