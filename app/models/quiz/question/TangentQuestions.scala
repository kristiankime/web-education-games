package models.quiz.question

import com.artclod.mathml.slick.MathMLMapper.string2mathML
import com.artclod.slick.NumericBoolean
import com.artclod.slick.JodaUTC.timestamp2DateTime
import models.quiz.Quiz
import models.quiz.answer.TangentAnswer
import models.quiz.answer.table.TangentAnswersTable
import models.quiz.question.table.TangentQuestionsTable
import models.quiz.table._
import models.support._
import models.user.User
import models.user.table._
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import com.artclod.slick.listGroupBy

object TangentQuestions {

  // ======= CREATE ======
  def create(info: TangentQuestion, quizId: QuizId)(implicit session: Session): TangentQuestion = {
    val toInsert = info.copy(id = QuestionIdNext(), quizIdOp = Some(quizId))  // TODO setup order here
    tangentQuestionsTable += toInsert
    toInsert
  }

  // ======= FIND ======
  def list()(implicit session: Session) = tangentQuestionsTable.list

  protected[question] def apply(questionId: QuestionId)(implicit session: Session) = tangentQuestionsTable.where(_.id === questionId).firstOption

  def apply(qid: QuestionId, owner: User)(implicit session: Session) = tangentAnswersTable.where(a => a.questionId === qid && a.ownerId === owner.id).sortBy(_.creationDate).list

  def answers(qid: QuestionId)(implicit session: Session) = tangentAnswersTable.where(_.questionId === qid).sortBy(_.creationDate).list

  def answersAndOwners(qid: QuestionId)(implicit session: Session) =
    (for (
      a <- tangentAnswersTable if a.questionId === qid;
      u <- userTable if u.userId === a.ownerId
    ) yield (a, u)).sortBy( aU => (aU._2.name, aU._1.creationDate)).list

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: TangentQuestion)(implicit session: Session) =
    tangentQuestionsTable.where(_.id === question.id).update(question.copy(quizIdOp = None))

  // ======= RESULTS =======
  def results(user: User, asOfOp: Option[DateTime] = None, quizOp: Option[Quiz] = None)(questionTable: TableQuery[TangentQuestionsTable], answerTable: TableQuery[TangentAnswersTable])(implicit session: Session) = {
    val resultsRelational = Questions.resultsQuery[TangentQuestion, TangentQuestionsTable, TangentAnswer, TangentAnswersTable](user, asOfOp, quizOp)(questionTable, answerTable)
    val grouped = listGroupBy(resultsRelational)(_._1, _._2)
    grouped.map(v => TangentQuestionResults(user, v.key, v.values))
  }

  def correctResults(user: User, num: Int)(questionTable: TableQuery[TangentQuestionsTable], answerTable: TableQuery[TangentAnswersTable])(implicit session: Session) =
    Questions.correct[TangentQuestion, TangentQuestionsTable, TangentAnswer, TangentAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

  def incorrectResults(user: User, num: Int)(questionTable: TableQuery[TangentQuestionsTable], answerTable: TableQuery[TangentAnswersTable])(implicit session: Session) =
    Questions.incorrect[TangentQuestion, TangentQuestionsTable, TangentAnswer, TangentAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

}

