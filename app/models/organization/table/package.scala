package models.organization

import play.api.db.slick.Config.driver.simple._

package object table {

  val coursesQuizzesTable = TableQuery[CoursesQuizzesTable]
  val coursesTable = TableQuery[CoursesTable]
  val sectionsQuizzesTable = TableQuery[SectionsQuizzesTable]
  val sectionsTable = TableQuery[SectionsTable]
  val usersCoursesTable = TableQuery[UsersCoursesTable]
  val usersSectionsTable = TableQuery[UsersSectionsTable]

}
