package models.quiz

import com.artclod.collection.MustHandle
import models.quiz.answer.table.{DerivativeGraphAnswersTable, DerivativeAnswersTable, TangentAnswersTable}
import models.quiz.question.table.{DerivativeGraphQuestionsTable, DerivativeQuestionsTable, TangentQuestionsTable}
import play.api.db.slick.Config.driver.simple._

package object table {

  val quizzesTable = TableQuery[QuizzesTable]
  val usersQuizzesTable = TableQuery[Users2QuizzesTable]

  val derivativeQuestionsTable = TableQuery[DerivativeQuestionsTable]
  val derivativeGraphQuestionsTable = TableQuery[DerivativeGraphQuestionsTable]
  val tangentQuestionsTable = TableQuery[TangentQuestionsTable]
  val questionTables = MustHandle(derivativeQuestionsTable, derivativeGraphQuestionsTable, tangentQuestionsTable)

  val derivativeAnswersTable = TableQuery[DerivativeAnswersTable]
  val derivativeGraphAnswersTable = TableQuery[DerivativeGraphAnswersTable]
  val tangentAnswersTable = TableQuery[TangentAnswersTable]
  val answerTables = MustHandle(derivativeAnswersTable, derivativeGraphAnswersTable, tangentAnswersTable)

  case class QuestionAndAnswer[Q,A](question: Q, answer: A)

  val questionAndAnswerTables = MustHandle(
    QuestionAndAnswer(derivativeQuestionsTable, derivativeAnswersTable),
    QuestionAndAnswer(derivativeGraphQuestionsTable, derivativeGraphAnswersTable),
    QuestionAndAnswer(tangentQuestionsTable, tangentAnswersTable))

}
