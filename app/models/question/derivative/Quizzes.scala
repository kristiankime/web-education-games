package models.question.derivative

import play.api.db.slick.Config.driver.simple._
import models.organization.table._
import models.question.derivative.table._
import models.support._
import org.joda.time.DateTime
import service._
import models.organization._
import viewsupport.question.derivative._
import models.organization.assignment.table.AssignmentGroup2Quiz
import models.organization.assignment.table.AssignmentGroupsQuizzesTable

case class QuizTmp(owner: UserId, name: String, date: DateTime) {
  def apply(id: QuizId) = Quiz(id, owner, name, date, date)
}

case class Quiz(id: QuizId, owner: UserId, name: String, creationDate: DateTime, updateDate: DateTime) extends Secured {

  def results(student: User)(implicit session: Session) = UserQuizResults(student, this, questions.map(v => v.results(student)))

  def questions(implicit session: Session) = Quizzes.questions(id)

  def remove(question: Question)(implicit session: Session) = Questions.remove(this, question)

  def results(section: Section)(implicit session: Session): QuizResults = QuizResults(this, section.results(this))

  protected def linkAccess(implicit user: User, session: Session) = Quizzes.linkAccess(this)

  def access(implicit user: User, session: Session) = (Quizzes.courses(id).map(_.access.ceilEdit) :+ directAccess) max

}

object Quizzes {

  // ======= CREATE ======
  def create(info: QuizTmp)(implicit session: Session) : Quiz = {
    val quizId = (new QuizzesTable).insert(info)
    info(quizId)
  }

  def create(info: QuizTmp, courseId: CourseId)(implicit session: Session) : Quiz = {
    val quiz = create(info)
    (new CoursesQuizzesTable).insert(Course2Quiz(courseId, quiz.id))
    quiz
  }

  def create(info: QuizTmp, groupId :GroupId)(implicit session: Session) : Quiz = {
    val quiz = create(info)
    (new AssignmentGroupsQuizzesTable).insert(AssignmentGroup2Quiz(groupId, quiz.id))
    quiz
  }

  // ======= FIND ======
  def apply(quizId: QuizId)(implicit session: Session) =
    Query(new QuizzesTable).where(_.id === quizId).firstOption

  def apply(courseId: CourseId)(implicit session: Session) =
    (for (
      q <- (new QuizzesTable);
      cq <- (new CoursesQuizzesTable) if cq.quizId === q.id && cq.courseId === courseId
    ) yield q).list

  def questions(quizId: QuizId)(implicit session: Session) =
    (for {
      l <- (new QuizzesQuestionsTable) if l.quizId === quizId
      q <- (new QuestionsTable) if l.questionId === q.id
    } yield q).list

  def courses(quizId: QuizId)(implicit session: Session) =
    (for (
      c <- (new CoursesTable);
      cq <- (new CoursesQuizzesTable) if cq.courseId === c.id && cq.quizId === quizId
    ) yield c).list

  // ======= UPDATE ======
  def rename(quizId: QuizId, name: String)(implicit session: Session) =
    Query(new QuizzesTable).where(_.id === quizId).firstOption match {
      case Some(quiz) => {
        Query(new QuizzesTable).where(_.id === quizId).update(quiz.copy(name = name)); true
      }
      case None => false
    }

  // ======= AUTHORIZATION ======
  def linkAccess(quiz: Quiz)(implicit user: User, session: Session) =
    Query(new UsersQuizzesTable).where(uq => uq.userId === user.id && uq.id === quiz.id).firstOption.map(_.access).toAccess

}
