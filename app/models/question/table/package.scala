package models.question

import models.question.derivative.table.{DerivativeAnswersTable, DerivativeQuestionsTable}
import play.api.db.slick.Config.driver.simple._

package object table {

  val answersTable = TableQuery[DerivativeAnswersTable]
  val questionsTable = TableQuery[DerivativeQuestionsTable]
  val quizzesTable = TableQuery[QuizzesTable]
  val usersQuizzesTable = TableQuery[Users2QuizzesTable]

}
