package models.quiz

import com.artclod.collection.MustHandle
import models.quiz.answer.table._
import models.quiz.question.table._
import play.api.db.slick.Config.driver.simple._

package object table {

  val quizzesTable = TableQuery[QuizzesTable]
  val usersQuizzesTable = TableQuery[Users2QuizzesTable]

  val derivativeQuestionsTable = TableQuery[DerivativeQuestionsTable]
  val derivativeGraphQuestionsTable = TableQuery[DerivativeGraphQuestionsTable]
  val tangentQuestionsTable = TableQuery[TangentQuestionsTable]
  val graphMatchQuestionsTable = TableQuery[GraphMatchQuestionsTable]
  val polynomialZoneQuestionsTable = TableQuery[PolynomialZoneQuestionsTable]
  val multipleChoiceQuestionsTable = TableQuery[MultipleChoiceQuestionsTable]; val multipleChoiceQuestionOptionsTable = TableQuery[MultipleChoiceQuestionOptionsTable]
  val questionTables = MustHandle(derivativeQuestionsTable, derivativeGraphQuestionsTable, tangentQuestionsTable, graphMatchQuestionsTable, polynomialZoneQuestionsTable, multipleChoiceQuestionsTable)

  val derivativeAnswersTable = TableQuery[DerivativeAnswersTable]
  val derivativeGraphAnswersTable = TableQuery[DerivativeGraphAnswersTable]
  val tangentAnswersTable = TableQuery[TangentAnswersTable]
  val graphMatchAnswersTable = TableQuery[GraphMatchAnswersTable]
  val polynomialZoneAnswersTable = TableQuery[PolynomialZoneAnswersTable]
  val multipleChoiceAnswersTable = TableQuery[MultipleChoiceAnswersTable]
  val answerTables = MustHandle(derivativeAnswersTable, derivativeGraphAnswersTable, tangentAnswersTable, graphMatchAnswersTable, polynomialZoneAnswersTable, multipleChoiceAnswersTable)

  case class QuestionAndAnswer[Q,A](question: Q, answer: A)

  val questionAndAnswerTables = MustHandle.fromTuple(questionTables.zip(answerTables) -> (
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2),
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2),
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2),
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2),
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2),
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2)
    ))
}
