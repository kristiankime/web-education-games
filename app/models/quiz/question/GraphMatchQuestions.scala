package models.quiz.question

import com.artclod.slick.listGroupBy
import com.google.common.annotations.VisibleForTesting
import models.quiz._
import models.quiz.answer.{GraphMatchAnswer, DerivativeGraphAnswer}
import models.quiz.answer.table.{GraphMatchAnswersTable, DerivativeGraphAnswersTable}
import models.quiz.question.table.{GraphMatchQuestion2QuizTable, DerivativeQuestion2QuizTable, GraphMatchQuestionsTable, DerivativeGraphQuestionsTable}
import models.quiz.table._
import models.support._
import models.user.User
import models.user.table.usersTable
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

object GraphMatchQuestions {

  // ======= CREATE ======
  def create(info: GraphMatchQuestion, quizId: QuizId)(implicit session: Session): GraphMatchQuestion = {
    val toInsert = create(info)

    val quizLink = Question2Quiz(toInsert.id, quizId, toInsert.ownerId, toInsert.creationDate, 1) // TODO setup order here
    graphMatchQuestion2QuizTable += quizLink

    toInsert
  }

  def create(info: GraphMatchQuestion)(implicit session: Session): GraphMatchQuestion = {
    val toInsert = info.copy(id = QuestionIdNext())
    graphMatchQuestionsTable += toInsert
    toInsert
  }

  // ======= FIND ======
  def list()(implicit session: Session) = graphMatchQuestionsTable.list

  protected[question] def apply(questionId: QuestionId)(implicit session: Session) = graphMatchQuestionsTable.where(_.id === questionId).firstOption

  def apply(qid: QuestionId, owner: User)(implicit session: Session) = graphMatchAnswersTable.where(a => a.questionId === qid && a.ownerId === owner.id).sortBy(_.creationDate).list

  def answers(qid: QuestionId)(implicit session: Session) = graphMatchAnswersTable.where(_.questionId === qid).sortBy(_.creationDate).list

  def answersAndOwners(qid: QuestionId)(implicit session: Session) =
    (for (
      a <- graphMatchAnswersTable if a.questionId === qid;
      u <- usersTable if u.userId === a.ownerId
    ) yield (a, u)).sortBy( aU => (aU._2.name, aU._1.creationDate)).list

  def quizFor(questionId: QuestionId, quizId: QuizId)(implicit session: Session) =
    (for (
      q <- graphMatchQuestion2QuizTable if q.questionId === questionId && q.quizId === quizId;
      z <- quizzesTable if z.id === q.quizId
    ) yield z).firstOption

//  def quizFor(questionId: QuestionId)(implicit session: Session) =
//    (for (
//      q <- graphMatchQuestionsTable if q.id === questionId;
//      z <- quizzesTable if z.id === q.quizId
//    ) yield z).firstOption

  def answerers(questionId: QuestionId)(implicit session: Session) = {
    val userIds = graphMatchAnswersTable.where(_.questionId === questionId).groupBy(_.ownerId).map({ case (ownerId, query) => ownerId})
    val users = service.table.LoginsTable.loginTable.where(_.id in userIds).sortBy(_.email)
    users.list
  }

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: GraphMatchQuestion)(implicit session: Session) =
    graphMatchQuestion2QuizTable.where(r => r.questionId === question.id && r.quizId === quiz.id).delete
//  graphMatchQuestionsTable.where(_.id === question.id).update(question.copy(quizIdOp = None))

  // ======= RESULTS =======
  def results(user: User, asOfOp: Option[DateTime] = None, quizOp: Option[Quiz] = None)(questionTable: TableQuery[GraphMatchQuestionsTable], answerTable: TableQuery[GraphMatchAnswersTable], question2QuizTable: TableQuery[GraphMatchQuestion2QuizTable])(implicit session: Session) = {
    val resultsRelational = Questions.resultsQuery[GraphMatchQuestion, GraphMatchQuestionsTable, GraphMatchAnswer, GraphMatchAnswersTable, GraphMatchQuestion2QuizTable](user, asOfOp, quizOp)(questionTable, answerTable, question2QuizTable)
    val grouped = listGroupBy(resultsRelational)(_._1, _._2)
    grouped.map(v => GraphMatchQuestionResults(user, v.key, v.values))
  }

  def correctResults(user: User, num: Int)(questionTable: TableQuery[GraphMatchQuestionsTable], answerTable: TableQuery[GraphMatchAnswersTable])(implicit session: Session) =
    Questions.correct[GraphMatchQuestion, GraphMatchQuestionsTable, GraphMatchAnswer, GraphMatchAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

  def incorrectResults(user: User, num: Int)(questionTable: TableQuery[GraphMatchQuestionsTable], answerTable: TableQuery[GraphMatchAnswersTable])(implicit session: Session) =
    Questions.incorrect[GraphMatchQuestion, GraphMatchQuestionsTable, GraphMatchAnswer, GraphMatchAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

}