package models.question.derivative

import models.question.table.{Users2QuizzesTable, QuizzesTable}
import play.api.db.slick.Config.driver.simple._

package object table {

  val answersTable = TableQuery[DerivativeAnswersTable]
  val questionsTable = TableQuery[DerivativeQuestionsTable]
  val quizzesQuestionsTable = TableQuery[Quizzes2DerivativeQuestionsTable]
  val quizzesTable = TableQuery[QuizzesTable]
  val usersQuizzesTable = TableQuery[Users2QuizzesTable]

}
