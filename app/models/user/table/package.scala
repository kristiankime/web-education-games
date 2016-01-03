package models.user

import com.artclod.collection.MustHandle
import models.quiz.question.table.{GraphMatchQuestionsTable, TangentQuestionsTable, DerivativeGraphQuestionsTable, DerivativeQuestionsTable}
import play.api.db.slick.Config.driver.simple._

package object table {

  val usersTable = TableQuery[UsersTable]

  val gameCompletedAlertsTable = TableQuery[GameCompletedAlertsTable]

  val alertTables = MustHandle(gameCompletedAlertsTable)

}
