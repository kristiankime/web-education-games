package models.question

import models.question.derivative.table.{DerivativeAnswersTable, DerivativeQuestionsTable}
import models.question.tangent.table.{TangentAnswersTable, TangentQuestionsTable}
import play.api.db.slick.Config.driver.simple._

package object table {
  val derivativeAnswersTable = TableQuery[DerivativeAnswersTable]
  val derivativeQuestionsTable = TableQuery[DerivativeQuestionsTable]
  val tangentAnswersTable = TableQuery[TangentAnswersTable]
  val tangentQuestionsTable = TableQuery[TangentQuestionsTable]
  val quizzesTable = TableQuery[QuizzesTable]
  val usersQuizzesTable = TableQuery[Users2QuizzesTable]
}
