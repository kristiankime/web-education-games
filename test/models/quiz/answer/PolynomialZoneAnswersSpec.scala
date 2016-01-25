package models.quiz.answer

import com.artclod.math.Interval
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

@RunWith(classOf[JUnitRunner])
class PolynomialZoneAnswersSpec extends Specification {

  def pZQ(zoneType: PolynomialZoneType, roots: Int*) = PolynomialZoneQuestion(QuestionId(0), UserId(0), Vector(roots:_*), 1d, zoneType,JodaUTC(0), 0d, None, 1)

  def pZQ(roots: Vector[Int], zoneType: PolynomialZoneType) = PolynomialZoneQuestion(QuestionId(0), UserId(0), roots, 1d, zoneType, JodaUTC(0), 0d, None, 1)

  "correct" should {
		"return true if user inputs correct zones" in {
      PolynomialZoneAnswers.correct(pZQ(FirstDerivativeIncreasing, 0), Vector( Interval(0d, Double.PositiveInfinity) ))
    }
  }


  "splitPolyAtRoots" should {
    "return (-inf,+inf) for no roots" in {
      PolynomialZoneAnswers.splitPolyAtRoots(Vector[Int]()) must beEqualTo(Vector(Interval(Double.NegativeInfinity, Double.PositiveInfinity)))
    }

    "return (-inf,root),(root,+inf) split for a single root" in {
      PolynomialZoneAnswers.splitPolyAtRoots(Vector(0)) must beEqualTo(Vector(Interval(Double.NegativeInfinity, 0), Interval(0, Double.PositiveInfinity)))
    }

    "return (-inf,root1),(root1,root2), ...(rootn,+inf) split for multiple roots" in {
      PolynomialZoneAnswers.splitPolyAtRoots(Vector(-1,0,1)) must beEqualTo(
        Vector(Interval(Double.NegativeInfinity, -1), Interval(-1, 0), Interval(0, 1), Interval(1, Double.PositiveInfinity))
      )
    }
  }

  "keepRoots" should {
    "return (0,+inf) for roots (0) and increasing" in {
      PolynomialZoneAnswers.keepRoots(pZQ(FirstDerivativeIncreasing, 0)) must beEqualTo(Vector(Interval(0, Double.PositiveInfinity)))
    }

    "return (-inf,-1)(1,+inf) for roots (-1,1) and increasing" in {
      PolynomialZoneAnswers.keepRoots(pZQ(FirstDerivativeIncreasing, -1,1)) must beEqualTo(Vector(Interval(Double.NegativeInfinity, -1), Interval(1, Double.PositiveInfinity)))
    }
  }

}
