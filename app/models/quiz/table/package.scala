package models.quiz

import models.quiz.answer.table.{DerivativeAnswersTable, TangentAnswersTable}
import models.quiz.question.table.{DerivativeQuestionsTable, TangentQuestionsTable}
import play.api.db.slick.Config.driver.simple._

package object table {
  val derivativeAnswersTable = TableQuery[DerivativeAnswersTable]
  val derivativeQuestionsTable = TableQuery[DerivativeQuestionsTable]
  val tangentAnswersTable = TableQuery[TangentAnswersTable]
  val tangentQuestionsTable = TableQuery[TangentQuestionsTable]
  val quizzesTable = TableQuery[QuizzesTable]
  val usersQuizzesTable = TableQuery[Users2QuizzesTable]
}
