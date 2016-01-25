package models.quiz.question

import com.artclod.mathml.scalar._
import com.artclod.slick.JodaUTC
import models.quiz.question.support.{FirstDerivativeIncreasing, PolynomialZoneType}
import models.support.{UserId, QuestionId}
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._

import scala.util.Success

@RunWith(classOf[JUnitRunner])
class PolynomialZoneQuestionSpec extends Specification {

  def pZQ(zoneType: PolynomialZoneType, roots: Int*) = PolynomialZoneQuestion(QuestionId(0), UserId(0), Vector(roots:_*), 1d, zoneType, JodaUTC(0), 0d, None, 1)

  def pZQ(roots: Vector[Int], zoneType: PolynomialZoneType) = PolynomialZoneQuestion(QuestionId(0), UserId(0), roots, 1d, zoneType, JodaUTC(0), 0d, None, 1)


	"polynomial" should {

		"be x^2 for double 0" in {
      pZQ(FirstDerivativeIncreasing, 0, 0).polynomial.evalT(("x", 1d)) must beEqualTo(Success(1d))
    }

  }

}
