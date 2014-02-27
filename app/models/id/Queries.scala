package models.id

import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Query
import service.table._
import service._
import scala.slick.lifted.BaseTypeMapper

object Queries {

	def owner[T, I](id: I, table: IdentifiedAndOwned[T, I])(implicit session: Session, evidence: BaseTypeMapper[I]) = {
		(for (
			r <- tableToQuery(table) if r.id === id;
			u <- tableToQuery(new UserTable) if u.id === r.owner
		) yield (r, u))
	}

	def owners[T, I](table: IdentifiedAndOwned[T, I])(implicit session: Session) = {
		(for (
			r <- tableToQuery(table);
			u <- tableToQuery(new UserTable) if u.id === r.owner
		) yield (r, u))
	}

	def access[T, L <: UserLinkRow, I](user: User, link: UserLink[L, I], ownerQuery: Query[(IdentifiedAndOwned[T, I], UserTable), (T, User)])(implicit session: Session, evidence: BaseTypeMapper[I]) = {
		for (
			(oq, l) <- ownerQuery.leftJoin(tableToQuery(link))
				.on((oq, l) => oq._1.id === l.id && l.userId === user.id)
		) yield (oq._1, oq._2, l.access.?)
	}

}