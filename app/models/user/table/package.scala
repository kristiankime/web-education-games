package models.user

import play.api.db.slick.Config.driver.simple._

package object table {

  val userInfosTable = TableQuery[UserSettingsTable]

}
