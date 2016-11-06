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
  val multipleChoiceQuestionsTable = TableQuery[MultipleChoiceQuestionsTable]
  val multipleFunctionQuestionsTable = TableQuery[MultipleFunctionQuestionsTable]
  val questionTables = MustHandle(derivativeQuestionsTable, derivativeGraphQuestionsTable, tangentQuestionsTable, graphMatchQuestionsTable, polynomialZoneQuestionsTable, multipleChoiceQuestionsTable, multipleFunctionQuestionsTable)

  val multipleChoiceQuestionOptionsTable = TableQuery[MultipleChoiceQuestionOptionsTable]
  val multipleFunctionQuestionOptionsTable = TableQuery[MultipleFunctionQuestionOptionsTable]

  val derivativeQuestion2QuizTable = TableQuery[DerivativeQuestion2QuizTable]
  val derivativeGraphQuestion2QuizTable = TableQuery[DerivativeGraphQuestion2QuizTable]
  val tangentQuestion2QuizTable = TableQuery[TangentQuestion2QuizTable]
  val graphMatchQuestion2QuizTable = TableQuery[GraphMatchQuestion2QuizTable]
  val polynomialZoneQuestion2QuizTable = TableQuery[PolynomialZoneQuestion2QuizTable]
  val multipleChoiceQuestion2QuizTable = TableQuery[MultipleChoiceQuestion2QuizTable]
  val multipleFunctionQuestion2QuizTable = TableQuery[MultipleFunctionQuestion2QuizTable]
  val question2QuizTables = MustHandle(derivativeQuestion2QuizTable, derivativeGraphQuestion2QuizTable, tangentQuestion2QuizTable, graphMatchQuestion2QuizTable, polynomialZoneQuestion2QuizTable, multipleChoiceQuestion2QuizTable, multipleFunctionQuestion2QuizTable)

  val questionAnd2QuizTables = questionTables.zip(question2QuizTables)


  val derivativeAnswersTable = TableQuery[DerivativeAnswersTable]
  val derivativeGraphAnswersTable = TableQuery[DerivativeGraphAnswersTable]
  val tangentAnswersTable = TableQuery[TangentAnswersTable]
  val graphMatchAnswersTable = TableQuery[GraphMatchAnswersTable]
  val polynomialZoneAnswersTable = TableQuery[PolynomialZoneAnswersTable]
  val multipleChoiceAnswersTable = TableQuery[MultipleChoiceAnswersTable]
  val multipleFunctionAnswersTable = TableQuery[MultipleFunctionAnswersTable]
  val answerTables = MustHandle(derivativeAnswersTable, derivativeGraphAnswersTable, tangentAnswersTable, graphMatchAnswersTable, polynomialZoneAnswersTable, multipleChoiceAnswersTable, multipleFunctionAnswersTable)

  val multipleFunctionAnswerOptionsTable = TableQuery[MultipleFunctionAnswerOptionsTable]

  case class QuestionAndAnswer[Q,A](question: Q, answer: A)

  val questionAndAnswerTables = MustHandle.fromTuple(questionTables.zip(answerTables) -> (
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2),
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2),
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2),
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2),
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2),
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2),
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2)
    ))

//  case class QuestionAnswer2Quiz[Q <: TableQuery[QuestionsTable[_]], A <: TableQuery[AnswersTable[_]], Q2Q <: TableQuery[Question2QuizTable]](question: Q, answer: A, quiz2: Q)
  case class QuestionAnswer2Quiz[Q, A, Q2Q](question: Q, answer: A, quiz2: Q2Q)

  val questionAnswerAnd2QuizTables = {
    val zip = questionTables.zip(answerTables, question2QuizTables)
    MustHandle.fromTuple(zip -> (
          qAndA => QuestionAnswer2Quiz(qAndA._1, qAndA._2, qAndA._3),
          qAndA => QuestionAnswer2Quiz(qAndA._1, qAndA._2, qAndA._3),
          qAndA => QuestionAnswer2Quiz(qAndA._1, qAndA._2, qAndA._3),
          qAndA => QuestionAnswer2Quiz(qAndA._1, qAndA._2, qAndA._3),
          qAndA => QuestionAnswer2Quiz(qAndA._1, qAndA._2, qAndA._3),
          qAndA => QuestionAnswer2Quiz(qAndA._1, qAndA._2, qAndA._3),
          qAndA => QuestionAnswer2Quiz(qAndA._1, qAndA._2, qAndA._3)
          )
    )
  }

}
