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
import play.api.db.slick.Config.driver.simple._

object TangentQuestions {

  // ======= CREATE ======
  def create(info: TangentQuestion, quizId: QuizId)(implicit session: Session): TangentQuestion = {
    val toInsert = info.copy(id = QuestionIdNext(), quizIdOp = Some(quizId))  // TODO setup order here
    tangentQuestionsTable += toInsert
    toInsert
  }

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: TangentQuestion)(implicit session: Session) =
    tangentQuestionsTable.where(_.id === question.id).update(question.copy(quizIdOp = None))

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

//  def correct(userId: UserId, questionTable: TableQuery[TangentQuestionsTable], answerTable: TableQuery[TangentAnswersTable])(implicit session: Session) = { // Type information provided here to help IDE
//    val query1 : Query[(TangentQuestionsTable, TangentAnswersTable), (TangentQuestion, TangentAnswer)] = for(q <- questionTable; a <- answerTable if a.ownerId === userId && q.id === a.questionId && a.correct === NumericBoolean.T) yield (q, a)
//    val query2 : Query[(Column[QuestionId], Query[(TangentQuestionsTable, TangentAnswersTable), (TangentQuestion, TangentAnswer)]), (QuestionId, Query[(TangentQuestionsTable, TangentAnswersTable), (TangentQuestion, TangentAnswer)])] = query1.groupBy(_._1.id)
//    val query3 = query2.map { case (questionId, qAndA) => (questionId, qAndA.map(_._2.creationDate).min) }
//    val query4 = query3.sortBy(_._2.desc)
//    query4.list.map(r => (r._1, r._2.get))
//  }

  def correctResults(user: User, num: Int, questionTable: TableQuery[TangentQuestionsTable], answerTable: TableQuery[TangentAnswersTable])(implicit session: Session) =
    Questions.correct[TangentQuestion, TangentQuestionsTable, TangentAnswer, TangentAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

//  def incorrect(userId: UserId, questionTable: TableQuery[TangentQuestionsTable], answerTable: TableQuery[TangentAnswersTable])(implicit session: Session) = { // Type information provided here to help IDE
//    val query1 : Query[(TangentQuestionsTable, TangentAnswersTable), (TangentQuestion, TangentAnswer)] = for(q <- questionTable; a <- answerTable if a.ownerId === userId && q.id === a.questionId) yield (q, a)
//    val query2 : Query[(Column[QuestionId], Query[(TangentQuestionsTable, TangentAnswersTable), (TangentQuestion, TangentAnswer)]), (QuestionId, Query[(TangentQuestionsTable, TangentAnswersTable), (TangentQuestion, TangentAnswer)])] = query1.groupBy(_._1.id)
//    val query3 = query2.map { case (questionId, qAndA) => (questionId, qAndA.map(_._2.correct).max, qAndA.map(_._2.creationDate).max) }
//    val query4 = query3.filter(_._2 === NumericBoolean.F) // Only include question that have no correct answer
//    val query5 = query4.sortBy(_._3.desc)
//    query5.list.map(r => (r._1, r._3.get))
//  }

  def incorrectResults(user: User, num: Int, questionTable: TableQuery[TangentQuestionsTable], answerTable: TableQuery[TangentAnswersTable])(implicit session: Session) =
    Questions.incorrect[TangentQuestion, TangentQuestionsTable, TangentAnswer, TangentAnswersTable](user.id, questionTable, answerTable).take(num).map(e => (apply(e._1).get.results(user), e._2))

}

