package models.id

import play.api.db.slick.Config.driver.simple._
import service.Access

trait UserLink[T, I] extends Table[T] {
	def id: Column[I]
	def userId: Column[UserId]	
	def access: Column[Access]
}