package models.organization.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.question.derivative._
import models.question.derivative.table._
import models.id._

case class User2Section(userId: UserId, sectionId: SectionId, access: Access) extends UserLinkRow

class UsersSectionsTable extends Table[User2Section]("users_sections") with UserLink[User2Section, SectionId] {
	def userId = column[UserId]("user_id", O.NotNull)
	def id = column[SectionId]("section_id", O.NotNull)
	def access = column[Access]("access", O.NotNull) 
	def * = userId ~ id ~ access <> (User2Section, User2Section.unapply _)

	def pk = primaryKey("users_sections_pk", (userId, id))

	def userIdFK = foreignKey("users_sections_user_fk", userId, new UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("users_sections_section_fk", id, new SectionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}