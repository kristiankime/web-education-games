package models.quiz

import com.artclod.collection.MustHandle
import models.quiz.answer.table.{GraphMatchAnswersTable, DerivativeGraphAnswersTable, DerivativeAnswersTable, TangentAnswersTable}
import models.quiz.question.table.{GraphMatchQuestionsTable, DerivativeGraphQuestionsTable, DerivativeQuestionsTable, TangentQuestionsTable}
import play.api.db.slick.Config.driver.simple._

package object table {

  val quizzesTable = TableQuery[QuizzesTable]
  val usersQuizzesTable = TableQuery[Users2QuizzesTable]

  val derivativeQuestionsTable = TableQuery[DerivativeQuestionsTable]
  val derivativeGraphQuestionsTable = TableQuery[DerivativeGraphQuestionsTable]
  val tangentQuestionsTable = TableQuery[TangentQuestionsTable]
  val graphMatchQuestionsTable = TableQuery[GraphMatchQuestionsTable]
  val questionTables = MustHandle(derivativeQuestionsTable, derivativeGraphQuestionsTable, tangentQuestionsTable, graphMatchQuestionsTable)

  val derivativeAnswersTable = TableQuery[DerivativeAnswersTable]
  val derivativeGraphAnswersTable = TableQuery[DerivativeGraphAnswersTable]
  val tangentAnswersTable = TableQuery[TangentAnswersTable]
  val graphMatchAnswersTable = TableQuery[GraphMatchAnswersTable]
  val answerTables = MustHandle(derivativeAnswersTable, derivativeGraphAnswersTable, tangentAnswersTable, graphMatchAnswersTable)

  case class QuestionAndAnswer[Q,A](question: Q, answer: A)

  val questionAndAnswerTables = MustHandle.fromTuple(questionTables.zip(answerTables) -> (
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2),
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2),
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2),
    qAndA => QuestionAndAnswer(qAndA._1, qAndA._2)
    ))
}
