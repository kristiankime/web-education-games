package mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.XML
import scala.xml.Text
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import org.specs2.matcher.Matcher

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplyDivideSpec extends Specification {

	"isZero" should {
		"return true if denominator is 0" in {
			ApplyDivide(Cn(0), Cn(5)).isZero must beTrue
		}

		"return false if denominator is not 0 " in {
			ApplyDivide(Cn(1), Cn(2)).isZero must beFalse
		}
	}

	"isOne" should {
		"return true if numerator and denominator are equal" in {
			ApplyDivide(Cn(5), Cn(5)).isOne must beTrue
		}

		"return false if numerator and denominator are not equal" in {
			ApplyDivide(Cn(4), Cn(2)).isOne must beFalse
		}
	}

	"simplify" should {
		"return 0 if isZero is true" in {
			ApplyDivide(Cn(0), Cn(6)).simplify must beEqualTo(Cn(0))
		}

		"return 1 if isOne is true" in {
			ApplyDivide(Cn(4), Cn(4)).simplify must beEqualTo(Cn(1))
		}
		
		"return numerator if denominator is 1" in {
			ApplyDivide(Cn(6), Cn(1)).simplify must beEqualTo(Cn(6))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyDivide(Ci("X"), Cn(3)).simplify must beEqualTo(ApplyDivide(Ci("X"), Cn(3)))
		}
	}

	"derivative" should {
		"obey the quotient rule: (f/g)' = (f'g + g'f)/g^2 (both terms dx are 0)" in {
			ApplyDivide(Cn(5), Cn(3)).derivative("X") must beEqualTo(Cn(0))
		}
	}
}