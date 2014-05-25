package models.question.derivative

import com.artclod.collection._
import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime
import service._
import models.support._
import models.organization._
import models.organization.table._
import models.organization.assignment.table._
import models.question.derivative.table._
import viewsupport.question.derivative._
import com.google.common.annotations.VisibleForTesting

case class Quiz(id: QuizId, owner: UserId, name: String, creationDate: DateTime, updateDate: DateTime) extends Secured {

  def results(student: User)(implicit session: Session) = UserQuizResults(student, this, questions.map(v => v.results(student)))

  def questions(implicit session: Session) = Quizzes.questions(id)

  def nextQuestion(question: Question)(implicit session: Session) = {
    questions.elementAfter(question)
  }

//  def groupQuestionsStudents(implicit session: Session) = {
//
//
//    group match {
//      case None => None
//      case Some(group) => {
//        (for(
////          g <- assignmentGroupsTable if g.id === group.id;
////          ug <- usersAssignmentGroupsTable if ug.id === group.id;
//          q4 <- questionsForTable if q4.groupId === group.id;
//        ) yield g)
//      }
//    }
//    Quizzes.groupFor(id)
//    ???
//  }

  def group(implicit session: Session) = Quizzes.groupFor(id)

  def rename(name: String)(implicit session: Session) = Quizzes.rename(id, name)

  def remove(question: Question)(implicit session: Session) = Questions.remove(this, question)

  def results(section: Section)(implicit session: Session): QuizResults = QuizResults(this, section.results(this))

  protected def linkAccess(implicit user: User, session: Session) = Quizzes.linkAccess(this)

  def access(implicit user: User, session: Session) = {
    val groupAccess = Access(group.map(_.access)).ceilEdit
    val courseAccess = Quizzes.courses(id).map(_.access.ceilEdit)
    (courseAccess :+ groupAccess :+ directAccess) max
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

  // TODO ensure that users only have one quiz per group
  def create(info: Quiz, groupId: GroupId)(implicit session: Session): Quiz = {
    val quiz = create(info)
    assignmentGroupsQuizzesTable.insert(AssignmentGroup2Quiz(groupId, quiz.id))
    quiz
  }

  // ======= FIND ======
  def apply(quizId: QuizId)(implicit session: Session) : Option[Quiz] = quizzesTable.where(_.id === quizId).firstOption

  def apply(courseId: CourseId)(implicit session: Session) : List[Quiz]  =
    (for (
      q <- quizzesTable;
      cq <- coursesQuizzesTable if cq.quizId === q.id && cq.courseId === courseId
    ) yield q).list

  def questions(quizId: QuizId)(implicit session: Session) =
    (for {
      l <- quizzesQuestionsTable if l.quizId === quizId
      q <- questionsTable if l.questionId === q.id
    } yield q).list

  def courses(quizId: QuizId)(implicit session: Session) =
    (for (
      c <- coursesTable;
      cq <- coursesQuizzesTable if cq.courseId === c.id && cq.quizId === quizId
    ) yield c).list

  def groupFor(quizId: QuizId)(implicit session: Session) =
    (for (
      ag2q <- assignmentGroupsQuizzesTable if ag2q.quizId === quizId;
      g <- assignmentGroupsTable if g.id === ag2q.groupId
    ) yield g).firstOption

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
    usersQuizzesTable.where(uq => uq.userId === user.id && uq.id === quiz.id).firstOption.map(_.access).toAccess

}
