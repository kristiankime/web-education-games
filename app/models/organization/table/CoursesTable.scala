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

object CoursesTable extends Table[Course]("courses") {
	def id = column[CourseId]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name", O.NotNull)
	def creationDate = column[DateTime]("creationDate")
	def updateDate = column[DateTime]("updateDate")
	def * = id ~ name ~ creationDate ~ updateDate <> (Course, Course.unapply _)

	def autoInc = name returning id
	
	def insert(t: CourseTmp)(implicit s: Session) = this.autoInc.insert(t.name)

}
