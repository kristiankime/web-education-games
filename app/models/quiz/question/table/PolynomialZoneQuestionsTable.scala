package models.quiz.question.table

import com.artclod.mathml.scalar._
import com.artclod.mathml.slick.MathMLMapper._
import models.quiz.question.support.PolynomialZoneType
import models.quiz.question.{PolynomialZoneQuestion, DerivativeQuestion}
import models.quiz.table.{QuestionIdNext, quizzesTable}
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable
import models.support._

import scala.slick.model.ForeignKeyAction

class PolynomialZoneQuestionsTable(tag: Tag) extends Table[PolynomialZoneQuestion](tag, "polynomial_zone_questions") with QuestionsTable[PolynomialZoneQuestion] {
	def roots = column[Vector[Int]]("roots")
	def scale = column[Double]("scale")
	def zoneType = column[PolynomialZoneType]("zone_type")

	def * = (id, ownerId, roots, scale, zoneType, creationDate, atCreationDifficulty, quizId, order) <> (PolynomialZoneQuestion.tupled, PolynomialZoneQuestion.unapply _)

	def idFK = foreignKey("polynomial_zone_questions__id_fk", id, QuestionIdNext.questionIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def ownerFK = foreignKey("polynomial_zone_questions__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def quizIdFK = foreignKey("polynomial_zone_questions__quiz_fk", quizId, quizzesTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
