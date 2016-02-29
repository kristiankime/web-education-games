package models.quiz.answer.table

import com.artclod.math.Interval
import com.artclod.mathml.scalar._
import com.artclod.mathml.slick.MathMLMapper.string2mathML
import models.quiz.answer.{PolynomialZoneAnswer, TangentAnswer}
import models.quiz.table.{AnswerIdNext, polynomialZoneQuestionsTable}
import play.api.db.slick.Config.driver.simple._
import service.table.LoginsTable
import models.support._

import scala.slick.model.ForeignKeyAction

class PolynomialZoneAnswersTable(tag: Tag) extends Table[PolynomialZoneAnswer](tag, "polynomial_zone_answers") with AnswersTable[PolynomialZoneAnswer] {
  def zones = column[Vector[Interval]]("zones")

	def * = (id, ownerId, questionId, zones, correct, comment, creationDate) <> (PolynomialZoneAnswer.tupled, PolynomialZoneAnswer.unapply _)

  def idFK  = foreignKey("polynomial_zone_answers__id_fk", id, AnswerIdNext.answerIdTable)(_.id, onDelete = ForeignKeyAction.Cascade)
  def ownerFK = foreignKey("polynomial_zone_answers__owner_fk", ownerId, LoginsTable.loginTable)(_.id, onDelete = ForeignKeyAction.Cascade)
	def questionFK = foreignKey("polynomial_zone_answers__question_fk", questionId, polynomialZoneQuestionsTable)(_.id, onDelete = ForeignKeyAction.Cascade)
}
