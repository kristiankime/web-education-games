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
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import com.artclod.util.optionElse

import scala.language.postfixOps

object Questions {

  // ======= FIND ======
  def list()(implicit session: Session) : List[Question] =
    questionTables.->(_.list, _.list, _.list, _.list).map(v => v._1 ++ v._2 ++ v._3 ++ v._4)

  def apply(questionId: QuestionId)(implicit session: Session) : Option[Question] =
    questionTables.->(
      _.where(_.id === questionId).firstOption,
      _.where(_.id === questionId).firstOption,
      _.where(_.id === questionId).firstOption,
      _.where(_.id === questionId).firstOption).map(v => v._1 ++ v._2 ++ v._3 ++ v._4 headOption)

  // ======= REMOVE ======
  def remove(quiz: Quiz, question: Question)(implicit session: Session) =
    question match {
      case q: DerivativeQuestion => DerivativeQuestions.remove(quiz, q)
      case q: DerivativeGraphQuestion => DerivativeGraphQuestions.remove(quiz, q)
      case q: TangentQuestion => TangentQuestions.remove(quiz, q)
      case q: GraphMatchQuestion => GraphMatchQuestions.remove(quiz, q)
    }

  // ======= RESULTS ======
  def results(user: User, asOfOp: Option[DateTime] = None, quizOp: Option[Quiz] = None)(implicit session: Session) =
    questionAndAnswerTables.->(
      t => DerivativeQuestions.results(user, asOfOp, quizOp)(t.question, t.answer),
      t => DerivativeGraphQuestions.results(user, asOfOp, quizOp)(t.question, t.answer),
      t => TangentQuestions.results(user, asOfOp, quizOp)(t.question, t.answer),
      t => GraphMatchQuestions.results(user, asOfOp, quizOp)(t.question, t.answer)).map(v => v._1 ++ v._2 ++ v._3)

  def correctResults(user: User, num: Int)(implicit session: Session) =
    questionAndAnswerTables.->(
      t => DerivativeQuestions.correctResults(user, num)(t.question, t.answer),
      t => DerivativeGraphQuestions.correctResults(user, num)(t.question, t.answer),
      t => TangentQuestions.correctResults(user, num)(t.question, t.answer),
      t => GraphMatchQuestions.correctResults(user, num)(t.question, t.answer)).map(v => v._1 ++ v._2 ++ v._3)

  def incorrectResults(user: User, num: Int)(implicit session: Session) =
    questionAndAnswerTables.->(
      t => DerivativeQuestions.incorrectResults(user, num)(t.question, t.answer),
      t => DerivativeGraphQuestions.incorrectResults(user, num)(t.question, t.answer),
      t => TangentQuestions.incorrectResults(user, num)(t.question, t.answer),
      t => GraphMatchQuestions.incorrectResults(user, num)(t.question, t.answer)).map(v => v._1 ++ v._2 ++ v._3)

  // ========================================================
  //GENERIC METHODS USED BY SPECIFIC QUESTION TYPE MODELS (SEE DerivativeQuestions, TangentQuestions ...)
  // ========================================================
  def resultsQuery[Q <: Question, QT <: QuestionsTable[Q], A <: Answer, AT <: AnswersTable[A]](user: User, asOfOp: Option[DateTime] = None, quizOp: Option[Quiz] = None)(questionTable: TableQuery[QT], answerTable: TableQuery[AT])(implicit session: Session) = {
    val questionsAndAnswers: Query[(QT, AT), (Q, A)] = (for { q <- questionTable; a <- answerTable if q.id === a.questionId && a.ownerId === user.id } yield (q, a))
    val answersAsOf = optionElse(asOfOp) { asOf => questionsAndAnswers.filter(_._2.creationDate <= asOf) } { questionsAndAnswers }
    val answersForQuiz = optionElse(quizOp) { quiz => answersAsOf.filter(_._1.quizId === quiz.id) } { answersAsOf }
    val summaryDataSorted = answersForQuiz.sortBy(r => (r._1.id, r._2.creationDate))
    summaryDataSorted.list
  }

  def correct[Q <: Question, QT <: QuestionsTable[Q], A <: Answer, AT <: AnswersTable[A]](userId: UserId, questionTable: TableQuery[QT], answerTable: TableQuery[AT])(implicit session: Session) = {
    // Type information provided here to help IDE
    val questionsAndCorrectAnswers : Query[(QT, AT), (Q, A)] =
      for(q <- questionTable; a <- answerTable if a.ownerId === userId && q.id === a.questionId && a.correct === NumericBoolean.T) yield (q, a)
    val groupByQuestion : Query[(Column[QuestionId], Query[(QT, AT), (Q, A)]), (QuestionId, Query[(QT, AT), (Q, A)])] =
      questionsAndCorrectAnswers.groupBy(_._1.id)
    val earliestAnswerTime = groupByQuestion.map { case (questionId, qAndA) => (questionId, qAndA.map(_._2.creationDate).min) }
    val questionAndTimeSorted = earliestAnswerTime.sortBy(_._2.desc)
    questionAndTimeSorted.list.map(r => (r._1, r._2.get))
  }

  def incorrect[Q <: Question, QT <: QuestionsTable[Q], A <: Answer, AT <: AnswersTable[A]](userId: UserId, questionTable: TableQuery[QT], answerTable: TableQuery[AT])(implicit session: Session) = {
    // Type information provided here to help IDE
    val questionAndAllAnswers : Query[(QT, AT), (Q, A)] =
      for(q <- questionTable; a <- answerTable if a.ownerId === userId && q.id === a.questionId) yield (q, a)
    val groupedByQuestion : Query[(Column[QuestionId], Query[(QT, AT), (Q, A)]), (QuestionId, Query[(QT, AT), (Q, A)])] =
      questionAndAllAnswers.groupBy(_._1.id)
    val earliestAnswerAndCorrect = groupedByQuestion.map { case (questionId, qAndA) => (questionId, qAndA.map(_._2.correct).max, qAndA.map(_._2.creationDate).max) }
    val allAnswersIncorrect = earliestAnswerAndCorrect.filter(_._2 === NumericBoolean.F) // Only include question that have no correct answer
    val questionAndTimeSorted = allAnswersIncorrect.sortBy(_._3.desc)
    questionAndTimeSorted.list.map(r => (r._1, r._3.get))
  }

}

