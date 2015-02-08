package models.quiz.question

import com.artclod.mathml.slick.MathMLMapper
import com.artclod.mathml.slick.MathMLMapper.string2mathML
import com.artclod.slick.NumericBoolean
import com.artclod.slick.JodaUTC.timestamp2DateTime
import com.google.common.annotations.VisibleForTesting
import models.quiz._
import models.quiz.answer.DerivativeAnswer
import models.quiz.answer.result.DerivativeQuestionScores
import models.quiz.answer.table.DerivativeAnswersTable
import models.quiz.question.table.DerivativeQuestionsTable
import models.quiz.table.{QuestionIdNext, derivativeAnswersTable, derivativeQuestionsTable, quizzesTable}
import models.support._
import models.user.User
import models.user.table.userTable
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.{Query, _}

object DerivativeQuestions {

  // ======= CREATE ======
  def create(info: DerivativeQuestion, quizId: QuizId)(implicit session: Session): DerivativeQuestion = {
    val toInsert = info.copy(id = QuestionIdNext(), quizIdOp = Some(quizId))  // TODO setup order here
    derivativeQuestionsTable += toInsert
    toInsert
  }

  @VisibleForTesting
  def create(info: DerivativeQuestion)(implicit session: Session): DerivativeQuestion = {
    val toInsert = info.copy(id = QuestionIdNext())
    derivativeQuestionsTable += toInsert
    toInsert
  }

  // ======= FIND ======
  def list()(implicit session: Session) = derivativeQuestionsTable.list

  protected[question] def apply(questionId: QuestionId)(implicit session: Session) = derivativeQuestionsTable.where(_.id === questionId).firstOption

  def apply(qid: QuestionId, owner: User)(implicit session: Session) = derivativeAnswersTable.where(a => a.questionId === qid && a.ownerId === owner.id).sortBy(_.creationDate).list

  def answers(qid: QuestionId)(implicit session: Session) = derivativeAnswersTable.where(_.questionId === qid).sortBy(_.creationDate).list

  def answersAndOwners(qid: QuestionId)(implicit session: Session) =
    (for (
      a <- derivativeAnswersTable if a.questionId === qid;
      u <- userTable if u.userId === a.ownerId
    ) yield (a, u)).sortBy( aU => (aU._2.name, aU._1.creationDate)).list

  def quizFor(questionId: QuestionId)(implicit session: Session) =
    (for (
      q <- derivativeQuestionsTable if q.id === questionId;
      z <- quizzesTable if z.id === q.quizId
    ) yield z).firstOption

  def answerers(questionId: QuestionId)(implicit session: Session) = {
    val userIds = derivativeAnswersTable.where(_.questionId === questionId).groupBy(_.ownerId).map({ case (ownerId, query) => ownerId})
    val users = service.table.LoginsTable.loginTable.where(_.id in userIds).sortBy(_.email)
    users.list
  }

//  def correct(userId: UserId, questionTable: TableQuery[DerivativeQuestionsTable], answerTable: TableQuery[DerivativeAnswersTable])(implicit session: Session) = { // Type information provided here to help IDE
//    val query1 : Query[(DerivativeQuestionsTable, DerivativeAnswersTable), (DerivativeQuestion, DerivativeAnswer)] = for(q <- questionTable; a <- answerTable if a.ownerId === userId && q.id === a.questionId && a.correct === NumericBoolean.T) yield (q, a)
//    val query2 : Query[(Column[QuestionId], Query[(DerivativeQuestionsTable, DerivativeAnswersTable), (DerivativeQuestion, DerivativeAnswer)]), (QuestionId, Query[(DerivativeQuestionsTable, DerivativeAnswersTable), (DerivativeQuestion, DerivativeAnswer)])] = query1.groupBy(_._1.id)
//    val query3 = query2.map { case (questionId, qAndA) => (questionId, qAndA.map(_._2.creationDate).min) }
//    val query4 = query3.sortBy(_._2.desc)
//    query4.list.map(r => (r._1, r._2.get))
//  }

  def correctResults(user: User, num: Int, questionTable: TableQuery[DerivativeQuestionsTable], answerTable: TableQuery[DerivativeAnswersTable])(implicit session: Session) =
    Questions.correct[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

//  def incorrect(userId: UserId, questionTable: TableQuery[DerivativeQuestionsTable], answerTable: TableQuery[DerivativeAnswersTable])(implicit session: Session) = { // Type information provided here to help IDE
//    val query1 : Query[(DerivativeQuestionsTable, DerivativeAnswersTable), (DerivativeQuestion, DerivativeAnswer)] = for(q <- questionTable; a <- answerTable if a.ownerId === userId && q.id === a.questionId) yield (q, a)
//    val query2 : Query[(Column[QuestionId], Query[(DerivativeQuestionsTable, DerivativeAnswersTable), (DerivativeQuestion, DerivativeAnswer)]), (QuestionId, Query[(DerivativeQuestionsTable, DerivativeAnswersTable), (DerivativeQuestion, DerivativeAnswer)])] = query1.groupBy(_._1.id)
//    val query3 = query2.map { case (questionId, qAndA) => (questionId, qAndA.map(_._2.correct).max, qAndA.map(_._2.creationDate).max) }
//    val query4 = query3.filter(_._2 === NumericBoolean.F) // Only include question that have no correct answer
//    val query5 = query4.sortBy(_._3.desc)
//    query5.list.map(r => (r._1, r._3.get))
//  }

  def incorrectResults(user: User, num: Int, questionTable: TableQuery[DerivativeQuestionsTable], answerTable: TableQuery[DerivativeAnswersTable])(implicit session: Session) =
    Questions.incorrect[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: DerivativeQuestion)(implicit session: Session) =
    derivativeQuestionsTable.where(_.id === question.id).update(question.copy(quizIdOp = None))

  // ======= Summary ======
  def summary(user: User)(implicit session: Session) = {
    val q: Query[(DerivativeQuestionsTable, DerivativeAnswersTable), (DerivativeQuestion, DerivativeAnswer)] =
      (for { q <- derivativeQuestionsTable; a <- derivativeAnswersTable if q.id === a.questionId && a.ownerId === user.id } yield (q, a))
    summaryFor(q)
  }

  def summary(user: User, asOf: DateTime)(implicit session: Session) = {
    val q: Query[(DerivativeQuestionsTable, DerivativeAnswersTable), (DerivativeQuestion, DerivativeAnswer)] =
      (for { q <- derivativeQuestionsTable; a <- derivativeAnswersTable if q.id === a.questionId && a.ownerId === user.id && a.creationDate <= asOf } yield (q, a))
    summaryFor(q)
  }

  def summary(user: User, quiz: Quiz)(implicit session: Session) = {
    val q: Query[(DerivativeQuestionsTable, DerivativeAnswersTable), (DerivativeQuestion, DerivativeAnswer)] =
      (for { q <- derivativeQuestionsTable if q.quizId === quiz.id; a <- derivativeAnswersTable if q.id === a.questionId && a.ownerId === user.id } yield (q, a))
    summaryFor(q)
  }

  def summary(user: User, asOf: DateTime, quiz: Quiz)(implicit session: Session) = {
    val q: Query[(DerivativeQuestionsTable, DerivativeAnswersTable), (DerivativeQuestion, DerivativeAnswer)] =
      (for { q <- derivativeQuestionsTable if q.quizId === quiz.id; a <- derivativeAnswersTable if q.id === a.questionId && a.ownerId === user.id && a.creationDate <= asOf} yield (q, a))
    summaryFor(q)
  }

  private def summaryFor(q: Query[(DerivativeQuestionsTable, DerivativeAnswersTable), (DerivativeQuestion, DerivativeAnswer)])(implicit session: Session) : List[DerivativeQuestionScores] = {
    // This line is mostly type information for the IDE
    val q2 : Query[(Column[QuestionId], Query[(DerivativeQuestionsTable, DerivativeAnswersTable),(DerivativeQuestion, DerivativeAnswer)]),(QuestionId, Query[(DerivativeQuestionsTable, DerivativeAnswersTable),(DerivativeQuestion, DerivativeAnswer)])] = q.groupBy(_._1.id)
    val q3 = q2.map { case (questionId, qAndA) => (questionId, qAndA.length, qAndA.map(_._1.mathML).max, qAndA.map(_._1.rawStr).max, qAndA.map(_._2.correct).max, qAndA.map(_._2.creationDate).min) }
    val q4 = q3.sortBy(_._6)
    q4.list.map(r => DerivativeQuestionScores(r._1, r._2, r._3.get, r._4.get, NumericBoolean(r._5.get), r._6.get))
  }

}

