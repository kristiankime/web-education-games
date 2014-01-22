package models.organization.table

import play.api.db.slick.Config.driver.simple._
import mathml._
import mathml.scalar._
import models._
import models.question.derivative._
import models.organization._
import models.id._
import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._

object SectionsTable extends Table[Section]("sections") {
	def id = column[SectionId]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def courseId = column[CourseId]("courseId", O.NotNull)
	def creationDate = column[DateTime]("creationDate")
	def updateDate = column[DateTime]("updateDate")
	def * = id ~ name ~ courseId  ~ creationDate ~ updateDate <> (Section, Section.unapply _)

	def autoInc = name ~ courseId ~ creationDate ~ updateDate returning id
	
	def insert(t: SectionTmp)(implicit s: Session) = this.autoInc.insert(t.name, t.courseId, t.date, t.date)

}
