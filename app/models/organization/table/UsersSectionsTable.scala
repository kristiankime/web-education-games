package models.organization.table

import scala.slick.session.Session
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import service.table._
import service._
import models.question.derivative._
import models.question.derivative.table._
import models.id._

case class User2Section(userId: UserId, sectionId: SectionId, access: Access)

object UsersSectionsTable extends Table[User2Section]("users_sections") {
	def userId = column[UserId]("user_id", O.NotNull)
	def sectionId = column[SectionId]("section_id", O.NotNull)
	def access = column[Access]("access", O.NotNull) 
	def * = userId ~ sectionId ~ access <> (User2Section, User2Section.unapply _)

	def pk = primaryKey("users_sections_pk", (userId, sectionId))

	def userIdFK = foreignKey("users_sections_user_fk", userId, UserTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionIdFK = foreignKey("users_sections_section_fk", sectionId, SectionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}