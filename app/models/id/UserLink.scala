package models.id

import play.api.db.slick.Config.driver.simple._
import service.Access

trait UserLink[T <: UserLinkRow, I] extends Table[T] {
	def id: Column[I]
	def userId: Column[UserId]	
	def access: Column[Access]
}

trait UserLinkRow {
	val userId: UserId
	val access: Access
}