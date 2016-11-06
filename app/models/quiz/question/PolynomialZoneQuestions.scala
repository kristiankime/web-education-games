package models.quiz.question

import com.artclod.slick.listGroupBy
import models.quiz.Quiz
import models.quiz.table.polynomialZoneQuestionsTable
import models.quiz.answer.PolynomialZoneAnswer
import models.quiz.answer.table.PolynomialZoneAnswersTable
import models.quiz.question.table.{PolynomialZoneQuestion2QuizTable, PolynomialZoneQuestionsTable}
import models.quiz.table._
import models.support._
import models.user.User
import models.user.table._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._

object PolynomialZoneQuestions {

  // ======= CREATE ======
  def create(info: PolynomialZoneQuestion, quizId: QuizId)(implicit session: Session): PolynomialZoneQuestion = {
    val toInsert = info.copy(id = QuestionIdNext())  // TODO setup order here
    polynomialZoneQuestionsTable += toInsert

    val quizLink = Question2Quiz(toInsert.id, quizId, toInsert.ownerId, toInsert.creationDate, 1) // TODO setup order here
    polynomialZoneQuestion2QuizTable += quizLink

    toInsert
  }

  // ======= FIND ======
  def list()(implicit session: Session) = polynomialZoneQuestionsTable.list

  protected[question] def apply(questionId: QuestionId)(implicit session: Session) = polynomialZoneQuestionsTable.where(_.id === questionId).firstOption

  def apply(qid: QuestionId, owner: User)(implicit session: Session) = polynomialZoneAnswersTable.where(a => a.questionId === qid && a.ownerId === owner.id).sortBy(_.creationDate).list

  def answers(qid: QuestionId)(implicit session: Session) = polynomialZoneAnswersTable.where(_.questionId === qid).sortBy(_.creationDate).list

  def answersAndOwners(qid: QuestionId)(implicit session: Session) =
    (for (
      a <- polynomialZoneAnswersTable if a.questionId === qid;
      u <- usersTable if u.userId === a.ownerId
    ) yield (a, u)).sortBy( aU => (aU._2.name, aU._1.creationDate)).list

  def quizFor(questionId: QuestionId, quizId: QuizId)(implicit session: Session) =
    (for (
      q <- polynomialZoneQuestion2QuizTable if q.questionId === questionId && q.quizId === quizId;
      z <- quizzesTable if z.id === q.quizId
    ) yield z).firstOption

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: PolynomialZoneQuestion)(implicit session: Session) =
    polynomialZoneQuestion2QuizTable.where(r => r.questionId === question.id && r.quizId === quiz.id).delete
//  polynomialZoneQuestionsTable.where(_.id === question.id).update(question.copy(quizIdOp = None))

  // ======= RESULTS =======
  def results(user: User, asOfOp: Option[DateTime] = None, quizOp: Option[Quiz] = None)(questionTable: TableQuery[PolynomialZoneQuestionsTable], answerTable: TableQuery[PolynomialZoneAnswersTable], question2QuizTable: TableQuery[PolynomialZoneQuestion2QuizTable])(implicit session: Session) = {
    val resultsRelational = Questions.resultsQuery[PolynomialZoneQuestion, PolynomialZoneQuestionsTable, PolynomialZoneAnswer, PolynomialZoneAnswersTable, PolynomialZoneQuestion2QuizTable](user, asOfOp, quizOp)(questionTable, answerTable, question2QuizTable)
    val grouped = listGroupBy(resultsRelational)(_._1, _._2)
    grouped.map(v => PolynomialZoneQuestionResults(user, v.key, v.values))
  }

  def correctResults(user: User, num: Int)(questionTable: TableQuery[PolynomialZoneQuestionsTable], answerTable: TableQuery[PolynomialZoneAnswersTable])(implicit session: Session) =
    Questions.correct[PolynomialZoneQuestion, PolynomialZoneQuestionsTable, PolynomialZoneAnswer, PolynomialZoneAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

  def incorrectResults(user: User, num: Int)(questionTable: TableQuery[PolynomialZoneQuestionsTable], answerTable: TableQuery[PolynomialZoneAnswersTable])(implicit session: Session) =
    Questions.incorrect[PolynomialZoneQuestion, PolynomialZoneQuestionsTable, PolynomialZoneAnswer, PolynomialZoneAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

}

