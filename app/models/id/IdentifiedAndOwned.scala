package models.id

import play.api.db.slick.Config.driver.simple._

trait IdentifiedAndOwned[T, I] extends Table[T] {
	def id: Column[I]
	def owner: Column[UserId]
}

trait IdentifiedAndOwnedRow {
	val ownerId: UserId
}