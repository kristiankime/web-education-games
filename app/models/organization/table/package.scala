package models.organization

import play.api.db.slick.Config.driver.simple._

package object table {

  val coursesQuizzesTable = TableQuery[Courses2QuizzesTable]
  val coursesTable = TableQuery[CoursesTable]
  val sectionsQuizzesTable = TableQuery[SectionsQuizzesTable]
  val sectionsTable = TableQuery[SectionsTable]
  val usersCoursesTable = TableQuery[Users2CoursesTable]
  val usersSectionsTable = TableQuery[Users2SectionsTable]

}
