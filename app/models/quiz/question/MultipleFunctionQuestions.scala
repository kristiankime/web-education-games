package models.quiz.question

import com.artclod.slick.listGroupBy
import com.google.common.annotations.VisibleForTesting
import models.quiz._
import models.quiz.answer.MultipleFunctionAnswer
import models.quiz.answer.table.MultipleFunctionAnswersTable
import models.quiz.question.table.{MultipleFunctionQuestion2QuizTable, MultipleChoiceQuestion2QuizTable, MultipleFunctionQuestionsTable}
import models.quiz.table._
import models.support._
import models.user.User
import models.user.table.usersTable
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

object MultipleFunctionQuestions {

  // ======= CREATE ======
  def create(info: MultipleFunctionQuestion, options: List[MultipleFunctionQuestionOption], quizId: QuizId)(implicit session: Session): MultipleFunctionQuestion = {
    val toInsert = info.copy(id = QuestionIdNext())  // TODO setup order here
    multipleFunctionQuestionsTable += toInsert
    multipleFunctionQuestionOptionsTable ++= options.map(_.copy(questionId = toInsert.id))

    val quizLink = Question2Quiz(toInsert.id, quizId, toInsert.ownerId, toInsert.creationDate, 1) // TODO setup order here
    multipleFunctionQuestion2QuizTable += quizLink

    toInsert
  }

  @VisibleForTesting
  def create(info: MultipleFunctionQuestion, options: List[MultipleFunctionQuestionOption])(implicit session: Session): MultipleFunctionQuestion = {
    val toInsert = info.copy(id = QuestionIdNext())
    multipleFunctionQuestionsTable += toInsert
    multipleFunctionQuestionOptionsTable ++= options.map(_.copy(questionId = toInsert.id))
    toInsert
  }

  // ======= FIND ======
  def list()(implicit session: Session) = multipleFunctionQuestionsTable.list

  protected[question] def apply(questionId: QuestionId)(implicit session: Session) = multipleFunctionQuestionsTable.where(_.id === questionId).firstOption

  def apply(qid: QuestionId, owner: User)(implicit session: Session) = multipleFunctionAnswersTable.where(a => a.questionId === qid && a.ownerId === owner.id).sortBy(_.creationDate).list

  def answers(qid: QuestionId)(implicit session: Session) = multipleFunctionAnswersTable.where(_.questionId === qid).sortBy(_.creationDate).list

  def answersAndOwners(qid: QuestionId)(implicit session: Session) =
    (for (
      a <- multipleFunctionAnswersTable if a.questionId === qid;
      u <- usersTable if u.userId === a.ownerId
    ) yield (a, u)).sortBy( aU => (aU._2.name, aU._1.creationDate)).list

  def quizFor(questionId: QuestionId, quizId: QuizId)(implicit session: Session) =
    (for (
      q <- multipleFunctionQuestion2QuizTable if q.questionId === questionId && q.quizId === quizId;
      z <- quizzesTable if z.id === q.quizId
    ) yield z).firstOption

//  def quizFor(questionId: QuestionId)(implicit session: Session) =
//    (for (
//      q <- multipleFunctionQuestionsTable if q.id === questionId;
//      z <- quizzesTable if z.id === q.quizId
//    ) yield z).firstOption

  def answerers(questionId: QuestionId)(implicit session: Session) = {
    val userIds = multipleFunctionAnswersTable.where(_.questionId === questionId).groupBy(_.ownerId).map({ case (ownerId, query) => ownerId})
    val users = service.table.LoginsTable.loginTable.where(_.id in userIds).sortBy(_.email)
    users.list
  }

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: MultipleFunctionQuestion)(implicit session: Session) =
    multipleFunctionQuestion2QuizTable.where(r => r.questionId === question.id && r.quizId === quiz.id).delete
//  multipleFunctionQuestionsTable.where(_.id === question.id).update(question.copy(quizIdOp = None))

  // ======= RESULTS =======
  def results(user: User, asOfOp: Option[DateTime] = None, quizOp: Option[Quiz] = None)(questionTable: TableQuery[MultipleFunctionQuestionsTable], answerTable: TableQuery[MultipleFunctionAnswersTable], question2QuizTable: TableQuery[MultipleFunctionQuestion2QuizTable])(implicit session: Session) = {
    val resultsRelational = Questions.resultsQuery[MultipleFunctionQuestion, MultipleFunctionQuestionsTable, MultipleFunctionAnswer, MultipleFunctionAnswersTable, MultipleFunctionQuestion2QuizTable](user, asOfOp, quizOp)(questionTable, answerTable, question2QuizTable)
    val grouped = listGroupBy(resultsRelational)(_._1, _._2)
    grouped.map(v => MultipleFunctionQuestionResults(user, v.key, v.values))
  }

  def correctResults(user: User, num: Int)(questionTable: TableQuery[MultipleFunctionQuestionsTable], answerTable: TableQuery[MultipleFunctionAnswersTable])(implicit session: Session) =
    Questions.correct[MultipleFunctionQuestion, MultipleFunctionQuestionsTable, MultipleFunctionAnswer, MultipleFunctionAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

  def incorrectResults(user: User, num: Int)(questionTable: TableQuery[MultipleFunctionQuestionsTable], answerTable: TableQuery[MultipleFunctionAnswersTable])(implicit session: Session) =
    Questions.incorrect[MultipleFunctionQuestion, MultipleFunctionQuestionsTable, MultipleFunctionAnswer, MultipleFunctionAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

}