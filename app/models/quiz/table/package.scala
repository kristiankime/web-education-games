package models.quiz

import com.artclod.collection.MustHandle
import models.quiz.answer.table.{DerivativeAnswersTable, TangentAnswersTable}
import models.quiz.question.table.{DerivativeQuestionsTable, TangentQuestionsTable}
import play.api.db.slick.Config.driver.simple._

package object table {

  val quizzesTable = TableQuery[QuizzesTable]
  val usersQuizzesTable = TableQuery[Users2QuizzesTable]

  val derivativeQuestionsTable = TableQuery[DerivativeQuestionsTable]
  val tangentQuestionsTable = TableQuery[TangentQuestionsTable]
  val questionTables = MustHandle(derivativeQuestionsTable, tangentQuestionsTable)

  val derivativeAnswersTable = TableQuery[DerivativeAnswersTable]
  val tangentAnswersTable = TableQuery[TangentAnswersTable]


}
