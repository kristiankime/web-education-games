package models.question.derivative

import com.artclod.mathml.scalar._
import com.google.common.annotations.VisibleForTesting
import models.organization.Course
import models.question.{Correct2Short, QuestionScore, Quiz, ViewableMath}
import models.question.derivative.result.{QuestionSummary, QuestionResults}
import models.question.derivative.table._
import models.support._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.Config.driver.simple.Query
import service._
import service.table.UsersTable
import com.artclod.slick.JodaUTC.timestamp2DateTime
import models.question.derivative.table.MathMLMapper.string2mathML
import com.artclod.mathml.scalar.MathMLElem
import scala.slick.lifted

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

  def answers(qid: QuestionId)(implicit session: Session) = answersTable.where(_.questionId === qid).sortBy(_.creationDate).list

  def answersAndOwners(qid: QuestionId)(implicit session: Session) =
    (for (
      a <- answersTable if a.questionId === qid;
      u <- UsersTable.userTable if u.id === a.ownerId
    ) yield (a, u)).sortBy( aU => (aU._2.email, aU._1.creationDate)).list

  def quizFor(questionId: QuestionId)(implicit session: Session) = {
    (for (
      q2q <- quizzesQuestionsTable if q2q.questionId === questionId;
      q <- quizzesTable if q.id === q2q.quizId
    ) yield q).firstOption
  }

  def answerers(questionId: QuestionId)(implicit session: Session) = {
    val userIds = answersTable.where(_.questionId === questionId).groupBy(_.ownerId).map({ case (ownerId, query) => ownerId})
    val users = service.table.UsersTable.userTable.where(_.id in userIds).sortBy(_.email)
    users.list
  }

  def correct(userId: UserId)(implicit session: Session) = { // Type information provided here to help IDE
    val query1 : Query[(QuestionsTable, AnswersTable), (Question, Answer)] = for(q <- questionsTable; a <- answersTable if a.ownerId === userId && q.id === a.questionId && a.correct === Correct2Short.T) yield (q, a)
    val query2 : Query[(Column[QuestionId], Query[(QuestionsTable, AnswersTable), (Question, Answer)]), (QuestionId, Query[(QuestionsTable, AnswersTable), (Question, Answer)])] = query1.groupBy(_._1.id)
    val query3 = query2.map { case (questionId, qAndA) => (questionId, qAndA.map(_._2.creationDate).min) }
    val query4 = query3.sortBy(_._2.desc)
    query4.list.map(r => (r._1, r._2.get))
  }

  def correctResults(user: User, num: Int)(implicit session: Session) = correct(user.id).take(num).map(e => (apply(e._1).get.results(user), e._2))

  def incorrect(userId: UserId)(implicit session: Session) = { // Type information provided here to help IDE
    val query1 : Query[(QuestionsTable, AnswersTable), (Question, Answer)] = for(q <- questionsTable; a <- answersTable if a.ownerId === userId && q.id === a.questionId) yield (q, a)
    val query2 : Query[(Column[QuestionId], Query[(QuestionsTable, AnswersTable), (Question, Answer)]), (QuestionId, Query[(QuestionsTable, AnswersTable), (Question, Answer)])] = query1.groupBy(_._1.id)
    val query3 = query2.map { case (questionId, qAndA) => (questionId, qAndA.map(_._2.correct).max, qAndA.map(_._2.creationDate).max) }
    val query4 = query3.filter(_._2 === Correct2Short.F) // Only include question that have no correct answer
    val query5 = query4.sortBy(_._3.desc)
    query5.list.map(r => (r._1, r._3.get))
  }

  def incorrectResults(user: User, num: Int)(implicit session: Session) = incorrect(user.id).take(num).map(e => (apply(e._1).get.results(user), e._2))

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: Question)(implicit session: Session) = quizzesQuestionsTable.where(r => r.questionId === question.id && r.quizId === quiz.id).delete

  // ======= Summary ======
  def summary(user: User)(implicit session: Session) = {
    val q: Query[(QuestionsTable, AnswersTable), (Question, Answer)] =
      (for {q <- questionsTable; a <- answersTable if q.id === a.questionId && a.ownerId === user.id} yield (q, a))
    summaryFor(q)
  }

  def summary(user: User, asOf: DateTime)(implicit session: Session) = {
    val q: Query[(QuestionsTable, AnswersTable), (Question, Answer)] =
      (for {q <- questionsTable; a <- answersTable if q.id === a.questionId && a.ownerId === user.id && a.creationDate <= asOf} yield (q, a))
    summaryFor(q)
  }

  def summary(user: User, quiz: Quiz)(implicit session: Session) = {
    val q: Query[(QuestionsTable, AnswersTable), (Question, Answer)] =
      (for {z <- quizzesQuestionsTable if z.quizId === quiz.id; q <- questionsTable if z.questionId === q.id; a <- answersTable if q.id === a.questionId && a.ownerId === user.id} yield (q, a))
    summaryFor(q)
  }

  def summary(user: User, asOf: DateTime, quiz: Quiz)(implicit session: Session) = {
    val q: Query[(QuestionsTable, AnswersTable), (Question, Answer)] =
      (for {z <- quizzesQuestionsTable if z.quizId === quiz.id; q <- questionsTable if z.questionId === q.id;  a <- answersTable if q.id === a.questionId && a.ownerId === user.id && a.creationDate <= asOf} yield (q, a))
    summaryFor(q)
  }

  private def summaryFor(q: Query[(QuestionsTable, AnswersTable), (Question, Answer)])(implicit session: Session) : List[QuestionSummary] = {
    // This line is mostly type information for the IDE
    val q2 : Query[(Column[QuestionId], Query[(QuestionsTable, AnswersTable),(Question, Answer)]),(QuestionId, Query[(QuestionsTable, AnswersTable),(Question, Answer)])] = q.groupBy(_._1.id)
    val q3 = q2.map { case (questionId, qAndA) => (questionId, qAndA.length, qAndA.map(_._1.mathML).max, qAndA.map(_._1.rawStr).max, qAndA.map(_._2.correct).max, qAndA.map(_._2.creationDate).min) }
    val q4 = q3.sortBy(_._6)
    q4.list.map(r => QuestionSummary(r._1, r._2, r._3.get, r._4.get, Correct2Short(r._5.get), r._6.get))
  }

}

