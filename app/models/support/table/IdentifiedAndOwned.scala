package models.support.table

import play.api.db.slick.Config.driver.simple._
import models.support.UserId

trait IdentifiedAndOwned[T, I] extends Table[T] {
	def id: Column[I]
	def owner: Column[UserId]
}

trait IdentifiedAndOwnedRow {
	val ownerId: UserId
}