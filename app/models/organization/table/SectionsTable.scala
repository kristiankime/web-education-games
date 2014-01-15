package models.organization.table

import play.api.db.slick.Config.driver.simple._
import mathml._
import mathml.scalar._
import models._
import models.question.derivative._
import models.organization._
import models.id._

object SectionsTable extends Table[Section]("sections") {
	def id = column[SectionId]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def * = id ~ name <> (Section, Section.unapply _)

	def autoInc = name returning id
	
	def insert(t: SectionTmp)(implicit s: Session) = this.autoInc.insert(t.name)

}
