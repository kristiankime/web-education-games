package models.quiz.question

import com.artclod.slick.listGroupBy
import com.google.common.annotations.VisibleForTesting
import models.quiz._
import models.quiz.answer.MultipleChoiceAnswer
import models.quiz.answer.table.MultipleChoiceAnswersTable
import models.quiz.question.table.MultipleChoiceQuestionsTable
import models.quiz.table.{QuestionIdNext, multipleChoiceAnswersTable, multipleChoiceQuestionOptionsTable, multipleChoiceQuestionsTable, quizzesTable}
import models.support._
import models.user.User
import models.user.table.usersTable
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

object MultipleChoiceQuestionOptions {

  // ======= CREATE ======
  def create(question: MultipleChoiceQuestion, options: List[MultipleChoiceQuestionOption])(implicit session: Session) = {
    multipleChoiceQuestionOptionsTable ++= options.map(_.copy(questionId = question.id))
  }


  // ======= FIND ======
  def list()(implicit session: Session) = multipleChoiceQuestionOptionsTable.list

  def apply(question: MultipleChoiceQuestion)(implicit session: Session) = multipleChoiceQuestionOptionsTable.where(_.questionId === question.id).list

//  protected[question] def apply(questionId: QuestionId)(implicit session: Session) = multipleChoiceQuestionsTable.where(_.id === questionId).firstOption
//
//  def apply(qid: QuestionId, owner: User)(implicit session: Session) = multipleChoiceAnswersTable.where(a => a.questionId === qid && a.ownerId === owner.id).sortBy(_.creationDate).list
//
//  def answers(qid: QuestionId)(implicit session: Session) = multipleChoiceAnswersTable.where(_.questionId === qid).sortBy(_.creationDate).list
//
//  def answersAndOwners(qid: QuestionId)(implicit session: Session) =
//    (for (
//      a <- multipleChoiceAnswersTable if a.questionId === qid;
//      u <- usersTable if u.userId === a.ownerId
//    ) yield (a, u)).sortBy( aU => (aU._2.name, aU._1.creationDate)).list
//
//  def quizFor(questionId: QuestionId)(implicit session: Session) =
//    (for (
//      q <- multipleChoiceQuestionsTable if q.id === questionId;
//      z <- quizzesTable if z.id === q.quizId
//    ) yield z).firstOption
//
//  def answerers(questionId: QuestionId)(implicit session: Session) = {
//    val userIds = multipleChoiceAnswersTable.where(_.questionId === questionId).groupBy(_.ownerId).map({ case (ownerId, query) => ownerId})
//    val users = service.table.LoginsTable.loginTable.where(_.id in userIds).sortBy(_.email)
//    users.list
//  }
//
//  // ======= REMOVE ======
//  def remove(quiz: Quiz, question: MultipleChoiceQuestion)(implicit session: Session) =
//    multipleChoiceQuestionsTable.where(_.id === question.id).update(question.copy(quizIdOp = None))
//
//  // ======= RESULTS =======
//  def results(user: User, asOfOp: Option[DateTime] = None, quizOp: Option[Quiz] = None)(questionTable: TableQuery[MultipleChoiceQuestionsTable], answerTable: TableQuery[MultipleChoiceAnswersTable])(implicit session: Session) = {
//    val resultsRelational = Questions.resultsQuery[MultipleChoiceQuestion, MultipleChoiceQuestionsTable, MultipleChoiceAnswer, MultipleChoiceAnswersTable](user, asOfOp, quizOp)(questionTable, answerTable)
//    val grouped = listGroupBy(resultsRelational)(_._1, _._2)
//    grouped.map(v => MultipleChoiceQuestionResults(user, v.key, v.values))
//  }
//
//  def correctResults(user: User, num: Int)(questionTable: TableQuery[MultipleChoiceQuestionsTable], answerTable: TableQuery[MultipleChoiceAnswersTable])(implicit session: Session) =
//    Questions.correct[MultipleChoiceQuestion, MultipleChoiceQuestionsTable, MultipleChoiceAnswer, MultipleChoiceAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))
//
//  def incorrectResults(user: User, num: Int)(questionTable: TableQuery[MultipleChoiceQuestionsTable], answerTable: TableQuery[MultipleChoiceAnswersTable])(implicit session: Session) =
//    Questions.incorrect[MultipleChoiceQuestion, MultipleChoiceQuestionsTable, MultipleChoiceAnswer, MultipleChoiceAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

}