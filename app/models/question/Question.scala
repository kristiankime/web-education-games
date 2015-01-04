package models.question

import models.organization.Course
import models.support.{Owned, QuizId, UserId, QuestionId}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.Session
import play.api.templates.Html
import service.{Access, User}

trait Question extends Owned {
  val id: QuestionId
  val ownerId: UserId
  val creationDate: DateTime
  val quizIdOp: Option[QuizId]
  val order: Int

  def quiz(implicit session: Session) = quizIdOp.flatMap(Quizzes(_))

  def access(course: Course)(implicit user: User, session: Session) = {
    val courseAccess = course.access
    val ownerAccess = Access(user, ownerId)
    Seq(courseAccess, ownerAccess).max
  }

  def results(user: User)(implicit session: Session) : QuestionResults

  def answersAndOwners(implicit session: Session) : List[(Answer, User)]

  def difficulty : Double

  def display : Html
}
