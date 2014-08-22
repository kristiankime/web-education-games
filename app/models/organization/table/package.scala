package models.organization

import models.game.table.GamesTable
import play.api.db.slick.Config.driver.simple._

package object table {

  val organizationsTable = TableQuery[OrganizationsTable]
  val coursesQuizzesTable = TableQuery[Courses2QuizzesTable]
  val coursesTable = TableQuery[CoursesTable]
  val gamesTable = TableQuery[GamesTable]
  val usersCoursesTable = TableQuery[Users2CoursesTable]

}
