package models.quiz.question

import com.artclod.slick.listGroupBy
import com.google.common.annotations.VisibleForTesting
import models.quiz._
import models.quiz.answer.DerivativeGraphAnswer
import models.quiz.answer.table.DerivativeGraphAnswersTable
import models.quiz.question.table._
import models.quiz.table._
import models.support._
import models.user.User
import models.user.table.usersTable
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

import scala.slick.direct.order

object DerivativeGraphQuestions {

  // ======= CREATE ======
  def create(info: DerivativeGraphQuestion, quizId: QuizId)(implicit session: Session): DerivativeGraphQuestion = {
    val toInsert = info.copy(id = QuestionIdNext())
    derivativeGraphQuestionsTable += toInsert

    val quizLink = Question2Quiz(toInsert.id, quizId, toInsert.ownerId, toInsert.creationDate, 1) // TODO setup order here
    derivativeGraphQuestion2QuizTable += quizLink

    toInsert
  }

  @VisibleForTesting
  def create(info: DerivativeGraphQuestion)(implicit session: Session): DerivativeGraphQuestion = {
    val toInsert = info.copy(id = QuestionIdNext())
    derivativeGraphQuestionsTable += toInsert
    toInsert
  }

  // ======= FIND ======
  def list()(implicit session: Session) = derivativeGraphQuestionsTable.list

  protected[question] def apply(questionId: QuestionId)(implicit session: Session) = derivativeGraphQuestionsTable.where(_.id === questionId).firstOption

  def apply(qid: QuestionId, owner: User)(implicit session: Session) = derivativeGraphAnswersTable.where(a => a.questionId === qid && a.ownerId === owner.id).sortBy(_.creationDate).list

  def answers(qid: QuestionId)(implicit session: Session) = derivativeGraphAnswersTable.where(_.questionId === qid).sortBy(_.creationDate).list

  def answersAndOwners(qid: QuestionId)(implicit session: Session) =
    (for (
      a <- derivativeGraphAnswersTable if a.questionId === qid;
      u <- usersTable if u.userId === a.ownerId
    ) yield (a, u)).sortBy( aU => (aU._2.name, aU._1.creationDate)).list

    def quizFor(questionId: QuestionId, quizId: QuizId)(implicit session: Session) =
      (for (
        q <- derivativeGraphQuestion2QuizTable if q.questionId === questionId && q.quizId === quizId;
        z <- quizzesTable if z.id === q.quizId
      ) yield z).firstOption

//  def quizFor(questionId: QuestionId)(implicit session: Session) =
//    (for (
//      q <- derivativeGraphQuestionsTable if q.id === questionId;
//      z <- quizzesTable if z.id === q.quizId
//    ) yield z).firstOption

  def answerers(questionId: QuestionId)(implicit session: Session) = {
    val userIds = derivativeGraphAnswersTable.where(_.questionId === questionId).groupBy(_.ownerId).map({ case (ownerId, query) => ownerId})
    val users = service.table.LoginsTable.loginTable.where(_.id in userIds).sortBy(_.email)
    users.list
  }

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: DerivativeGraphQuestion)(implicit session: Session) =
    derivativeGraphQuestion2QuizTable.where(r => r.questionId === question.id && r.quizId === quiz.id).delete
//    derivativeGraphQuestionsTable.where(_.id === question.id).update(question.copy(quizIdOp = None))

  // ======= RESULTS =======
  def results(user: User, asOfOp: Option[DateTime] = None, quizOp: Option[Quiz] = None)(questionTable: TableQuery[DerivativeGraphQuestionsTable], answerTable: TableQuery[DerivativeGraphAnswersTable], question2QuizTable: TableQuery[DerivativeGraphQuestion2QuizTable])(implicit session: Session) = {
    val resultsRelational = Questions.resultsQuery[DerivativeGraphQuestion, DerivativeGraphQuestionsTable, DerivativeGraphAnswer, DerivativeGraphAnswersTable, DerivativeGraphQuestion2QuizTable](user, asOfOp, quizOp)(questionTable, answerTable, question2QuizTable)
    val grouped = listGroupBy(resultsRelational)(_._1, _._2)
    grouped.map(v => DerivativeGraphQuestionResults(user, v.key, v.values))
  }

  def correctResults(user: User, num: Int)(questionTable: TableQuery[DerivativeGraphQuestionsTable], answerTable: TableQuery[DerivativeGraphAnswersTable])(implicit session: Session) =
    Questions.correct[DerivativeGraphQuestion, DerivativeGraphQuestionsTable, DerivativeGraphAnswer, DerivativeGraphAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

  def incorrectResults(user: User, num: Int)(questionTable: TableQuery[DerivativeGraphQuestionsTable], answerTable: TableQuery[DerivativeGraphAnswersTable])(implicit session: Session) =
    Questions.incorrect[DerivativeGraphQuestion, DerivativeGraphQuestionsTable, DerivativeGraphAnswer, DerivativeGraphAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

}