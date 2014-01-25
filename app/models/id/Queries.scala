package models.id

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Query
import service.table._
import service._

object Queries {

	def owners[T, I](table: IdentifiedAndOwned[T, I])(implicit session: Session) = {
		(for (
			r <- tableToQuery(table);
			u <- tableToQuery(new UserTable) if u.id === r.owner
		) yield (r, u))
	}

	def access[T, L, I](user: User, link: UserLink[L, I], ownerQuery: Query[(IdentifiedAndOwned[T, I], UserTable), (T, User)])(implicit session: Session, evidence: scala.slick.lifted.BaseTypeMapper[I])= {
		for ((oq, l) <- ownerQuery.leftJoin(tableToQuery(link)).on(_._1.id === _.id)) yield (oq._1, oq._2, l.access.?)
	}

}