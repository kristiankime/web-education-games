package models.quiz.answer

import com.artclod.math.Interval
import com.artclod.mathml.MathMLEq.{doubleCloseEnough, matchCombine}
import com.artclod.mathml.scalar.MathMLElem
import com.google.common.annotations.VisibleForTesting
import models.quiz.question.{PolynomialZoneQuestion, TangentQuestion}
import models.quiz.table._
import play.api.db.slick.Config.driver.simple._

object PolynomialZoneAnswers {

  def correct(question: PolynomialZoneQuestion, answer: Vector[Interval]) = {
    keepRoots(question).equals( answer.sortBy(_.lower) )
  }

  private def valueIn(interval : Interval): Double = interval match {
    case Interval(Double.NegativeInfinity, Double.PositiveInfinity) => 0d
    case Interval(Double.NegativeInfinity, v) => v - 1d
    case Interval(v, Double.PositiveInfinity) => v + 1d
    case Interval(l, u) => (l + u) / 2d
  }

  @VisibleForTesting
  def keepRoots(question: PolynomialZoneQuestion) : Vector[Interval] = {
    val poly = question.polynomial
    val splits = splitPolyAtRoots(question.roots)

    splits.filter(interval => {
      val v = poly.evalT(("x", valueIn(interval))).get
      if(question.zoneType.positive) { v > 0d } else { v < 0d }
    })
  }

  @VisibleForTesting
  def splitPolyAtRoots(roots: Vector[Int]) : Vector[Interval] = {
    val asDouble : Vector[Double] = roots.sorted.map(_.toDouble)
    val negInf = Vector(Double.NegativeInfinity) ++ asDouble
    val posInf = asDouble ++ Vector(Double.PositiveInfinity)

    for(e <- negInf.zip(posInf) ) yield {
      Interval(e._1, e._2)
    }
  }

  // ======= CREATE ======
  def createAnswer(answer: PolynomialZoneAnswer)(implicit session: Session) = {
    val toInsert = answer.copy(id = AnswerIdNext())
    polynomialZoneAnswersTable += toInsert
    toInsert
  }

  def foo(question: PolynomialZoneQuestion) = {
    val poly = question.polynomial

    question.roots
  }

}
