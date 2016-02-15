package models.quiz.answer

import com.artclod.math.Interval
import com.artclod.mathml.MathMLEq.{doubleCloseEnough, matchCombine}
import com.artclod.mathml.scalar.MathMLElem
import com.google.common.annotations.VisibleForTesting
import models.quiz.question.{PolynomialZoneQuestion, TangentQuestion}
import models.quiz.table._
import play.api.db.slick.Config.driver.simple._

import scala.collection.mutable.ArrayBuffer

object PolynomialZoneAnswers {

  // ======= CREATE ======
  def createAnswer(answer: PolynomialZoneAnswer)(implicit session: Session) = {
    val toInsert = answer.copy(id = AnswerIdNext())
    polynomialZoneAnswersTable += toInsert
    toInsert
  }

  // ======= CORRECT ======
  def correct(question: PolynomialZoneQuestion, answer: Vector[Interval]) = {
    val questionZones = mergeZones(roots2Zones(question))
    val answerZones = mergeZones(fillOutZones(answer, question.zoneType.positive))
    questionZones equals answerZones
  }

  private def valueIn(interval : Interval): Double = interval match {
    case Interval(Double.NegativeInfinity, Double.PositiveInfinity) => 0d
    case Interval(Double.NegativeInfinity, v) => v - 1d
    case Interval(v, Double.PositiveInfinity) => v + 1d
    case Interval(l, u) => (l + u) / 2d
  }

  @VisibleForTesting
  def roots2Zones(question: PolynomialZoneQuestion) : Vector[SignedInterval] = {
    val roots = question.roots.distinct.sorted.map(_.toDouble)
    val func = question.polynomial
    val ret = ArrayBuffer[SignedInterval]()

    for(pair <- (Double.NegativeInfinity +: roots) zip (roots :+ Double.PositiveInfinity)) yield {
      val i = Interval(pair._1, pair._2)
      val v = func.evalT(("x", valueIn(i))).get
      val p = if(question.zoneType.positive) { v > 0d } else { v < 0d }
      ret += SignedInterval(i, p)
    }
    ret.toVector
  }

  @VisibleForTesting
  def mergeZones(zones: Vector[SignedInterval]) : Vector[SignedInterval] =
    if(zones.isEmpty) {
      zones
    } else {
      val sortedZones = zones.sortBy(_.i.lower)
      val ret = ArrayBuffer[SignedInterval]()
      var i = 0
      var previous : SignedInterval = sortedZones(0)

      for(zone <- sortedZones.tail) {
        if(previous.i.upper == zone.i.lower && previous.p == zone.p) {
          previous = SignedInterval(previous.i.lower, zone.i.upper, zone.p) // If the zone is "next to" this zone and has the same sign merge them
        } else {
          ret += previous
          previous = zone
        }
      }

      ret += previous // There is always a "left over" interval we need to add

      ret.toVector
    }

  @VisibleForTesting
  def fillOutZones(zonesIn: Vector[Interval], default: Boolean) : Vector[SignedInterval] =
    if(zonesIn.isEmpty) {
      Vector(SignedInterval(Double.NegativeInfinity, Double.PositiveInfinity, !default))
    } else {
      val zones = zonesIn.sortBy(_.l)
      val ret = ArrayBuffer[SignedInterval]()
      var previous : Interval = null
      var i = 0

      // Handle the first one
      if(zones.head.lower.isNegInfinity) {
        ret += SignedInterval(zones.head, default)
        previous = zones.head
        i = 1;
      } else {
        previous = Interval(Double.NegativeInfinity, zones.head.lower)
        ret += SignedInterval(previous, !default)
      }

      // Handle the middle ones
      while(i < zones.size) {
        val zone = zones(i)
        if(zone.lower == previous.upper) {
          ret += SignedInterval(zone, default)
          previous = zone
          i += 1
        } else {
          previous = Interval(previous.upper, zone.lower)
          ret += SignedInterval(previous, !default)
        }
      }

      // Handle the final
      if(!ret.last.i.upper.isPosInfinity) {
        ret += SignedInterval(previous.upper, Double.PositiveInfinity, !default)
      }

      ret.toVector
    }

}

@VisibleForTesting
case class SignedInterval(i: Interval, p: Boolean)

@VisibleForTesting
object SignedInterval {
  def apply(lower: Double, upper: Double, positive: Boolean) : SignedInterval = SignedInterval(Interval(lower, upper), positive)
}