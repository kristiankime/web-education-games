package models.quiz.question

import com.artclod.mathml.slick.MathMLMapper.string2mathML
import com.artclod.slick.NumericBoolean
import com.artclod.slick.JodaUTC.timestamp2DateTime
import com.artclod.slick.NumericBoolean
import com.artclod.tuple._
import models.quiz._
import models.quiz.answer.Answer
import models.quiz.answer.table.AnswersTable
import models.quiz.question.table.QuestionsTable
import models.quiz.table._
import models.support._
import models.user.User
import play.api.db.slick.Config.driver.simple._

import scala.language.postfixOps

object Questions {

  // ======= FIND ======
  def list()(implicit session: Session) : List[Question] =
    questionTables.->(_.list, _.list).map(v => v._1 ++ v._2)

  def apply(questionId: QuestionId)(implicit session: Session) : Option[Question] =
    questionTables.->(_.where(_.id === questionId).firstOption, _.where(_.id === questionId).firstOption).map(v => v._1 ++ v._2 headOption)

  def correctResults(user: User, num: Int)(implicit session: Session) =
    questionAndAnswerTables.->(
      t => DerivativeQuestions.correctResults(user, num, t.question, t.answer),
      t => TangentQuestions.correctResults(user, num, t.question, t.answer) ).map(v => v._1 ++ v._2)

  def incorrectResults(user: User, num: Int)(implicit session: Session) =
    questionAndAnswerTables.->(
      t => DerivativeQuestions.incorrectResults(user, num, t.question, t.answer),
      t => TangentQuestions.incorrectResults(user, num, t.question, t.answer) ).map(v => v._1 ++ v._2)

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: Question)(implicit session: Session) = {
    question match {
      case q: DerivativeQuestion => DerivativeQuestions.remove(quiz, q)
      case q: TangentQuestion => TangentQuestions.remove(quiz, q)
    }
  }

  // ======= GENERIC METHODS USED BY SPECIFIC QUESTION TYPE MODELS =======
  def correct[Q <: Question, QT <: QuestionsTable[Q], A <: Answer, AT <: AnswersTable[A]](userId: UserId, questionTable: TableQuery[QT], answerTable: TableQuery[AT])(implicit session: Session) = {
    // Type information provided here to help IDE
    val query1 : Query[(QT, AT), (Q, A)] =
      for(q <- questionTable; a <- answerTable if a.ownerId === userId && q.id === a.questionId && a.correct === NumericBoolean.T) yield (q, a)
    val query2 : Query[(Column[QuestionId], Query[(QT, AT), (Q, A)]), (QuestionId, Query[(QT, AT), (Q, A)])] =
      query1.groupBy(_._1.id)
    val query3 = query2.map { case (questionId, qAndA) => (questionId, qAndA.map(_._2.creationDate).min) }
    val query4 = query3.sortBy(_._2.desc)
    query4.list.map(r => (r._1, r._2.get))
  }


  def incorrect[Q <: Question, QT <: QuestionsTable[Q], A <: Answer, AT <: AnswersTable[A]](userId: UserId, questionTable: TableQuery[QT], answerTable: TableQuery[AT])(implicit session: Session) = {
    // Type information provided here to help IDE
    val query1 : Query[(QT, AT), (Q, A)] =
      for(q <- questionTable; a <- answerTable if a.ownerId === userId && q.id === a.questionId) yield (q, a)
    val query2 : Query[(Column[QuestionId], Query[(QT, AT), (Q, A)]), (QuestionId, Query[(QT, AT), (Q, A)])] =
      query1.groupBy(_._1.id)
    val query3 = query2.map { case (questionId, qAndA) => (questionId, qAndA.map(_._2.correct).max, qAndA.map(_._2.creationDate).max) }
    val query4 = query3.filter(_._2 === NumericBoolean.F) // Only include question that have no correct answer
    val query5 = query4.sortBy(_._3.desc)
    query5.list.map(r => (r._1, r._3.get))
  }
}

