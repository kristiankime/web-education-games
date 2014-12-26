package models.question.derivative

import com.artclod.mathml.scalar.MathMLElem
import models.support.{UserId, QuestionId}
import com.artclod.mathml.scalar._
import com.google.common.annotations.VisibleForTesting
import models.organization.Course
import models.question.{Correct2Short, QuestionScore, Quiz, ViewableMath}
import models.question.derivative.result.QuestionResults
import models.question.derivative.table._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.Config.driver.simple.Query
import service._
import service.table.UsersTable
import models.question.derivative.result.QuestionResults
import com.artclod.slick.JodaUTC.timestamp2DateTime
import models.question.derivative.table.MathMLMapper.string2mathML
import com.artclod.mathml.scalar.MathMLElem
import scala.slick.lifted

case class Question(id: QuestionId, ownerId: UserId, mathML: MathMLElem, rawStr: String, creationDate: DateTime) extends ViewableMath with Owned {

  def quiz(implicit session: Session) = Questions.quizFor(id)

  def answersAndOwners(implicit session: Session) = Questions.answersAndOwners(id)

  def difficulty : Double = QuestionDifficulty(mathML)

  def results(user: User)(implicit session: Session) = QuestionResults(user, this, answers(user), start(user))

  def answers(user: User)(implicit session: Session) = Questions(id, user)

  def start(user: User)(implicit session: Session) = Answers.startedWork(user, id).map(_.time)

  def access(course: Course)(implicit user: User, session: Session) = {
    val cAccess = course.access
    val qAccess = Access(user, ownerId)
    Seq(cAccess, qAccess).max
  }

}
