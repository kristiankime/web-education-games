package models.question

import com.artclod.collection._
import com.google.common.annotations.VisibleForTesting
import models.organization._
import models.organization.table._
import models.question.derivative.result.QuizResults
import models.question.derivative.table._
import models.question.derivative.{DerivativeQuestion, DerivativeQuestions}
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service._
import scala.language.postfixOps

case class Quiz(id: QuizId, ownerId: UserId, name: String, creationDate: DateTime, updateDate: DateTime) extends Secured {

   def results(student: User)(implicit session: Session) = QuizResults(student, this, questions.map(v => v.results(student)))

   def results(course: Course)(implicit session: Session) : List[QuizResults] = course.students.map(results(_))

   def summary(student: User)(implicit session: Session) = DerivativeQuestions.summary(student, this)

   def summary(student: User, asOf: DateTime)(implicit session: Session) = DerivativeQuestions.summary(student, asOf, this)

   def questions(implicit session: Session) = Quizzes.questions(id)

   def previousQuestion(question: DerivativeQuestion)(implicit session: Session) = questions.elementBefore(question)

   def nextQuestion(question: DerivativeQuestion)(implicit session: Session) = questions.elementAfter(question)

   def firstUnfinishedQuestion(user: User)(implicit session: Session) = results(user).firstUnfinishedQuestion

   def rename(name: String)(implicit session: Session) = Quizzes.rename(id, name)

   def remove(question: DerivativeQuestion)(implicit session: Session) = DerivativeQuestions.remove(this, question)

   def course(courseId: CourseId)(implicit session: Session): Option[Course] = Quizzes.course(courseId, id)

   protected def linkAccess(implicit user: User, session: Session) = Quizzes.linkAccess(this)

   def access(implicit user: User, session: Session) = {
     val courseAccess = Quizzes.courses(id).map(_.access.ceilEdit).toSeq
     (courseAccess :+ directAccess) max
   }

   def studentScore(student: User)(implicit session: Session) = {
     val summaries = DerivativeQuestions.summary(student, this)
     summaries.map(_.studentScore).sum / questions.size.toDouble
   }

   def teacherScore(student: User, studentSkillLevel: Double)(implicit session: Session) = {
     val summaries = DerivativeQuestions.summary(student, this)
     summaries.map(_.teacherScore(studentSkillLevel)).sum / questions.size.toDouble
   }
 }
