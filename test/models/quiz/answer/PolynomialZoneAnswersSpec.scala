package models.quiz.answer

import com.artclod.math.Interval
import com.artclod.math.{Interval => I}
import com.artclod.slick.JodaUTC
import models.quiz.question.DerivativeDifficulty.Diff
import models.quiz.question.support.{FirstDerivativeIncreasing, PolynomialZoneType}
import models.quiz.question.{PolynomialZoneQuestion, DerivativeQuestionResults, QuestionScoring, TestDerivativeQuestion}
import models.support.{QuizId, UserId, QuestionId}
import models.user.UserSettingTest
import org.joda.time.DateTime
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import scala.Double.{PositiveInfinity => ∞, NegativeInfinity => -∞}
import scala.collection.immutable.{Vector => V }
import models.quiz.answer.{ SignedInterval => SI }

@RunWith(classOf[JUnitRunner])
class PolynomialZoneAnswersSpec extends Specification {

  def pZQ(zoneType: PolynomialZoneType, roots: Int*) = PolynomialZoneQuestion(QuestionId(0), UserId(0), Vector(roots:_*), 1d, zoneType, JodaUTC(0), 0d, 1)

  def pZQ(roots: Vector[Int], zoneType: PolynomialZoneType) = PolynomialZoneQuestion(QuestionId(0), UserId(0), roots, 1d, zoneType, JodaUTC(0), 0d, 1)

  "correct" should {
		"return true if user inputs correct zones for one root" in {
      PolynomialZoneAnswers.correct(pZQ(FirstDerivativeIncreasing, 0), V(I(0d, ∞)))
    }

    "return true if user inputs correct zones for two roots" in {
      PolynomialZoneAnswers.correct(pZQ(FirstDerivativeIncreasing, -1, 1), V(I(-∞, -1d), I(1d, ∞)))
    }

    "return true if user inputs correct zones for three roots" in {
      PolynomialZoneAnswers.correct(pZQ(FirstDerivativeIncreasing, -4, 5, 50), V(I(-4, 5), I(50, ∞)))
    }

    "return true if user inputs correct zones for three roots (duplicate root)" in {
      PolynomialZoneAnswers.correct(pZQ(FirstDerivativeIncreasing, 5, -4, 5), V(I(-4, 5), I(5, ∞)))
    }
  }

  "correct" should {
    "return true if zones match" in {
      PolynomialZoneAnswers.correct(pZQ(FirstDerivativeIncreasing, -1, 1), Vector(Interval(-∞, -1), Interval(1, ∞))) must beTrue
    }

    "return true if zones match (even if the input aren't sorted" in {
      PolynomialZoneAnswers.correct(pZQ(FirstDerivativeIncreasing, 1, -1), Vector(Interval(1, ∞), Interval(-∞, -1))) must beTrue
    }

    "return false if zones do not match" in {
      PolynomialZoneAnswers.correct(pZQ(FirstDerivativeIncreasing, -1, 2), Vector(Interval(-∞, -1), Interval(1, ∞))) must beFalse
    }
  }

  "fillOutZones" should {
    "return -Inf to Inf if nothing is specified" in {
      PolynomialZoneAnswers.fillOutZones(V(), true) must beEqualTo( V(SI(-∞, ∞, false)) )
    }

    "add -Inf before an ending zone" in {
      PolynomialZoneAnswers.fillOutZones(V(I(2d, ∞)), true) must beEqualTo( V(SI(-∞, 2d, false), SI(2d, ∞, true)) )
    }

    "add Inf after a starting zone" in {
      PolynomialZoneAnswers.fillOutZones(V(I(-∞, 6d)), true) must beEqualTo( V(SI(-∞, 6d, true), SI(6d, ∞, false)) )
    }

    "insert intermediate zones" in {
      PolynomialZoneAnswers.fillOutZones(V(I(-∞, -10d),                             I(4d, 8d),                           I(9d, ∞)), true) must beEqualTo(
                                        V(SI(-∞, -10d, true), SI(-10d, 4d, false), SI(4d, 8d, true), SI(8d, 9d, false), SI(9d, ∞, true) ) )
    }

    "not insert unnecessary intermediate zones" in {
      PolynomialZoneAnswers.fillOutZones(V(I(-∞, -10d),                             I(4d, 8d),        I(8d, 9d),        I(9d, ∞)), true) must beEqualTo(
                                        V(SI(-∞, -10d, true), SI(-10d, 4d, false), SI(4d, 8d, true), SI(8d, 9d, true), SI(9d, ∞, true) ) )
    }
  }

  "mergeZones" should {
    "do nothing if zones do not touch" in {
      PolynomialZoneAnswers.mergeZones(  V(SI(0, 2, false), SI(4, 6, false))  ) must beEqualTo(  V(SI(0, 2, false), SI(4, 6, false))  )
    }

    "merge zones of they touch" in {
      PolynomialZoneAnswers.mergeZones(  V(SI(0, 2, false), SI(2, 4, false), SI(4, 6, false))  ) must beEqualTo(  V(SI(0, 6, false))  )
    }

    "do not merge zones of they touch but have the wrong sign" in {
      PolynomialZoneAnswers.mergeZones(  V(SI(0, 2, false), SI(2, 4, true), SI(4, 6, false))  ) must beEqualTo(  V(SI(0, 2, false), SI(2, 4, true), SI(4, 6, false))  )
    }
  }

  "roots2Zones" should {
    "return one interval from -∞ to ∞" in {
      PolynomialZoneAnswers.roots2Zones( pZQ(FirstDerivativeIncreasing) ) must beEqualTo(  V(SI(-∞, ∞, true))  )
    }

    "create two zones with one root" in {
      PolynomialZoneAnswers.roots2Zones( pZQ(FirstDerivativeIncreasing, 1) ) must beEqualTo(  V(SI(-∞, 1, false), SI(1, ∞, true))  )
    }

    "create three zones with two different roots" in {
      PolynomialZoneAnswers.roots2Zones( pZQ(FirstDerivativeIncreasing, 1, 2) ) must beEqualTo(  V(SI(-∞, 1, true), SI(1, 2, false), SI(2, ∞, true))  )
    }

    "create three zones with three roots if two of them are duplicates" in {
      PolynomialZoneAnswers.roots2Zones( pZQ(FirstDerivativeIncreasing, 1, 2, 2) ) must beEqualTo(  V(SI(-∞, 1, false), SI(1, 2, true), SI(2, ∞, true))  )
    }
  }

}
