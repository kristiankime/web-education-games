package models.question.derivative

import com.artclod.collection._
import models.question.derivative.result.QuizResults
import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime
import service._
import models.support._
import models.organization._
import models.organization.table._
import models.question.derivative.table._
import viewsupport.question.derivative._
import com.google.common.annotations.VisibleForTesting
import models.user.UserPimped

case class Quiz(id: QuizId, ownerId: UserId, name: String, creationDate: DateTime, updateDate: DateTime) extends Secured {

  def results(student: User)(implicit session: Session) = QuizResults(student, this, questions.map(v => v.results(student)))

  def results(course: Course)(implicit session: Session) : List[QuizResults] = course.students.map(results(_))

  def summary(student: User)(implicit session: Session) = Questions.summary(student, this)

  def summary(student: User, asOf: DateTime)(implicit session: Session) = Questions.summary(student, asOf, this)

  def questions(implicit session: Session) = Quizzes.questions(id)

  def previousQuestion(question: Question)(implicit session: Session) = questions.elementBefore(question)

  def nextQuestion(question: Question)(implicit session: Session) = questions.elementAfter(question)

  def firstUnfinishedQuestion(user: User)(implicit session: Session) = results(user).firstUnfinishedQuestion

  def rename(name: String)(implicit session: Session) = Quizzes.rename(id, name)

  def remove(question: Question)(implicit session: Session) = Questions.remove(this, question)

  def course(courseId: CourseId)(implicit session: Session): Option[Course] = Quizzes.course(courseId, id)

  protected def linkAccess(implicit user: User, session: Session) = Quizzes.linkAccess(this)

  def access(implicit user: User, session: Session) = {
    val courseAccess = Quizzes.courses(id).map(_.access.ceilEdit).toSeq
    (courseAccess :+ directAccess) max
  }

  def studentScore(student: User)(implicit session: Session) = {
    val summaries = Questions.summary(student, this)
    summaries.map(_.studentScore).sum / questions.size.toDouble
  }

  def teacherScore(student: User, studentSkillLevel: Double)(implicit session: Session) = {
    val summaries = Questions.summary(student, this)
    summaries.map(_.teacherScore(studentSkillLevel)).sum / questions.size.toDouble
  }
}

object Quizzes {
  // ======= CREATE ======
  @VisibleForTesting
  def create(quiz: Quiz)(implicit session: Session) : Quiz = {
    val quizId: QuizId = (quizzesTable returning quizzesTable.map(_.id)) += quiz
    quiz.copy(id = quizId)
  }

  def create(info: Quiz, courseId: CourseId)(implicit session: Session): Quiz = {
    val quiz = create(info)
    coursesQuizzesTable.insert(Course2Quiz(courseId, quiz.id))
    quiz
  }

  // ======= FIND ======
  def apply(quizId: QuizId)(implicit session: Session) : Option[Quiz] = quizzesTable.where(_.id === quizId).firstOption

  def apply(courseId: CourseId)(implicit session: Session) : List[Quiz]  =
    (for (
      q <- quizzesTable;
      cq <- coursesQuizzesTable if cq.quizId === q.id && cq.courseId === courseId
    ) yield q).sortBy(_.creationDate).list

  def questions(quizId: QuizId)(implicit session: Session) : List[Question] =
    (for {
      l <- quizzesQuestionsTable if l.quizId === quizId
      q <- questionsTable if l.questionId === q.id
    } yield q).sortBy(_.creationDate).list

  def courses(quizId: QuizId)(implicit session: Session) : List[Course] =
    (for (
      c <- coursesTable;
      cq <- coursesQuizzesTable if cq.courseId === c.id && cq.quizId === quizId
    ) yield c).list

  def course(courseId: CourseId, quizId: QuizId)(implicit session: Session) : Option[Course] =
    (for (
      c <- coursesTable if c.id === courseId;
      cq <- coursesQuizzesTable if cq.courseId === c.id && cq.quizId === quizId
    ) yield c).firstOption

  // ======= UPDATE ======
  def rename(quizId: QuizId, name: String)(implicit session: Session) =
    quizzesTable.where(_.id === quizId).firstOption match {
      case Some(quiz) => {
        quizzesTable.where(_.id === quizId).update(quiz.copy(name = name));
        true
      }
      case None => false
    }

  // ======= AUTHORIZATION ======
  def linkAccess(quiz: Quiz)(implicit user: User, session: Session) =
    usersQuizzesTable.where(uq => uq.userId === user.id && uq.quizId === quiz.id).firstOption.map(_.access).toAccess

  // ======= Scoring ======
  def studentScore(quiz: Quiz, user: User)(implicit session: Session) = {
    quiz.questions.map( _.results(user))
  }

}
