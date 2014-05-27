package models.support.table

import play.api.db.slick.Config.driver.simple._
import service.Access
import models.support.UserId

trait UserLink[T <: UserLinkRow, I] extends Table[T] {
	def id: Column[I]
	def userId: Column[UserId]	
	def access: Column[Access]
}

trait UserLinkRow {
	val userId: UserId
	val access: Access
}