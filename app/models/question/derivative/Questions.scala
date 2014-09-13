package models.question.derivative

import com.artclod.mathml.scalar._
import models.organization.Course
import models.question.AsciiMathML
import models.question.derivative.table._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import service._
import service.table.UsersTable
import viewsupport.question.derivative.QuestionResults
import com.artclod.slick.JodaUTC.timestamp2DateTime
import models.question.derivative.table.MathMLMapper.string2mathML
import com.artclod.mathml.scalar.MathMLElem
import scala.slick.lifted

case class Question(id: QuestionId, ownerId: UserId, mathML: MathMLElem, rawStr: String, creationDate: DateTime) extends AsciiMathML with Owned {

  def quiz(implicit session: Session) = Questions.quizFor(id)

  def answersAndOwners(implicit session: Session) = Questions.answersAndOwners(id)

  def difficulty(implicit session: Session): Option[Double] = {
    val difficulties = Questions.answerers(id).flatMap(results(_).score).map(1d - _)
    if (difficulties.isEmpty) None
    else Some(difficulties.sum / difficulties.size)
  }

  def results(user: User)(implicit session: Session) = QuestionResults(user, this, answers(user), start(user))

  def answers(user: User)(implicit session: Session) = Questions(id, user)

  def start(user: User)(implicit session: Session) = Answers.startedWork(user, id).map(_.time)

  def access(course: Course)(implicit user: User, session: Session) = {
    val cAccess = course.access
    val qAccess = Access(user, ownerId)
    Seq(cAccess, qAccess).max
  }

}

object Questions {

  // ======= CREATE ======
  def create(info: Question, quizId: QuizId)(implicit session: Session): Question = {
    val questionId = (questionsTable returning questionsTable.map(_.id)) += info
    quizzesQuestionsTable += Quiz2Question(quizId, questionId)
    info.copy(id = questionId)
  }

  def create(info: Question)(implicit session: Session): Question = {
    val questionId = (questionsTable returning questionsTable.map(_.id)) += info
    info.copy(id = questionId)
  }

  // ======= FIND ======
  def list()(implicit session: Session) = questionsTable.list

  def apply(questionId: QuestionId)(implicit session: Session) = questionsTable.where(_.id === questionId).firstOption

  def apply(qid: QuestionId, owner: User)(implicit session: Session) = answersTable.where(a => a.questionId === qid && a.ownerId === owner.id).sortBy(_.creationDate).list

  def answers(qid: QuestionId)(implicit session: Session) = answersTable.where(_.questionId === qid).list

  def answersAndOwners(qid: QuestionId)(implicit session: Session) =
    (for (
      a <- answersTable if a.questionId === qid;
      u <- UsersTable.userTable if u.id === a.ownerId
    ) yield (a, u)).sortBy(_._2.lastName).list

  def quizFor(questionId: QuestionId)(implicit session: Session) = {
    (for (
      q2q <- quizzesQuestionsTable if q2q.questionId === questionId;
      q <- quizzesTable if q.id === q2q.quizId
    ) yield q).firstOption
  }

  def answerers(questionId: QuestionId)(implicit session: Session) = {
    val userIds = answersTable.where(_.questionId === questionId).groupBy(_.ownerId).map({ case (ownerId, query) => ownerId})
    val users = service.table.UsersTable.userTable.where(_.id in userIds).sortBy(_.lastName)
    users.list
  }

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: Question)(implicit session: Session) = quizzesQuestionsTable.where(r => r.questionId === question.id && r.quizId === quiz.id).delete

  // ======= Summary ======
  case class QuestionSummary(questionId: QuestionId, attempts: Int, mathMl: MathMLElem,  correct: Boolean, firstAttempt: DateTime)

  def summary(user: User)(implicit session: Session) = {
    val q: lifted.Query[(QuestionsTable, AnswersTable), (Question, Answer)] =
      (for {q <- questionsTable;
            a <- answersTable if q.id === a.questionId && a.ownerId === user.id} yield (q, a))
    val q2 = q.groupBy(_._1.id)
    val q3 = q2.map { case (questionId, qAndA) => (questionId, qAndA.length, qAndA.map(_._1.mathML).max, qAndA.map(_._2.correct).max, qAndA.map(_._2.creationDate).min) }
    q3.list.map(r => QuestionSummary(r._1, r._2, r._3.get, r._4.get, r._5.get))
  }
}

