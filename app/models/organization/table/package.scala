package models.organization

import play.api.db.slick.Config.driver.simple.TableQuery

package object table {

  val organizationsTable = TableQuery[OrganizationsTable]
  val coursesQuizzesTable = TableQuery[Courses2QuizzesTable]
  val coursesTable = TableQuery[CoursesTable]
  val usersCoursesTable = TableQuery[Users2CoursesTable]

}
