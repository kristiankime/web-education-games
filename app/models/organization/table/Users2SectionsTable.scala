package models.organization.table

import play.api.db.slick.Config.driver.simple._
import scala.slick.model.ForeignKeyAction
import service.table._
import service._
import models.organization.Sections
import models.support._
import scala.slick.model.ForeignKeyAction
import models.support.table.{UserLinkRow, UserLink}

case class User2Section(userId: UserId, sectionId: SectionId, access: Access) extends UserLinkRow

class Users2SectionsTable(tag: Tag) extends Table[User2Section](tag, "users_2_sections") with UserLink[User2Section, SectionId] {
	def userId = column[UserId]("user_id", O.NotNull)
	def id = column[SectionId]("section_id", O.NotNull)
	def access = column[Access]("access", O.NotNull) 
	def * = (userId, id, access) <> (User2Section.tupled, User2Section.unapply _)

	def pk = primaryKey("users_2_sections_pk", (userId, id))

	def userIdFK = foreignKey("users_2_sections_user_fk", userId, UsersTable.userTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("users_2_sections_section_fk", id, sectionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}