package models.quiz.question

import com.artclod.mathml.slick.MathMLMapper
import com.artclod.mathml.slick.MathMLMapper.string2mathML
import com.artclod.slick.NumericBoolean
import com.artclod.slick.JodaUTC.timestamp2DateTime
import com.google.common.annotations.VisibleForTesting
import models.quiz._
import models.quiz.answer.DerivativeAnswer
import models.quiz.answer.result.{DerivativeQuestionResults}
import models.quiz.answer.table.DerivativeAnswersTable
import models.quiz.question.table.DerivativeQuestionsTable
import models.quiz.table.{QuestionIdNext, derivativeAnswersTable, derivativeQuestionsTable, quizzesTable}
import models.support._
import models.user.User
import models.user.table.userTable
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple.{Query, _}
import com.artclod.util.optionElse
import com.artclod.slick.listGroupBy

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

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: DerivativeQuestion)(implicit session: Session) =
    derivativeQuestionsTable.where(_.id === question.id).update(question.copy(quizIdOp = None))

  // ======= Results =======
  def results(user: User, asOfOp: Option[DateTime] = None, quizOp: Option[Quiz] = None)(implicit session: Session) = {
    val resultsRelational = Questions.results[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user, asOfOp, quizOp)(derivativeQuestionsTable, derivativeAnswersTable)
    val grouped = listGroupBy(resultsRelational)(_._1, _._2)
    grouped.map(v => DerivativeQuestionResults(user, v.key, v.values))
  }

  def correctResults(user: User, num: Int, questionTable: TableQuery[DerivativeQuestionsTable], answerTable: TableQuery[DerivativeAnswersTable])(implicit session: Session) =
    Questions.correct[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

  def incorrectResults(user: User, num: Int, questionTable: TableQuery[DerivativeQuestionsTable], answerTable: TableQuery[DerivativeAnswersTable])(implicit session: Session) =
    Questions.incorrect[DerivativeQuestion, DerivativeQuestionsTable, DerivativeAnswer, DerivativeAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

}