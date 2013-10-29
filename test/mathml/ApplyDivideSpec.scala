package mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.XML
import scala.xml.Text
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import org.specs2.matcher.Matcher
import mathml.scalar.ApplyDivide
import mathml.scalar.Cn
import mathml.scalar.Ci

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

	"cn" should {
		"return division if numerator and denominator are numbers " in {
			(Cn(6) /Cn(4)).cnStep.get must beEqualTo(Cn(1.5))
		}
		
		"return zero if numerator is zero " in {
			(Cn(0) /Cn(4)).cnStep.get must beEqualTo(Cn(0))
		}
	}

	"simplify" should {
		"return 0 if numerator is 0 (and denominator is not)" in {
			ApplyDivide(Cn(0), Cn(6)).simplifyStep must beEqualTo(Cn(0))
		}

		"return 1 if numerator and denominator are equal (and non zero)" in {
			ApplyDivide(Cn(4), Cn(4)).simplifyStep must beEqualTo(Cn(1))
		}

		"return numerator if denominator is 1" in {
			ApplyDivide(Cn(6), Cn(1)).simplifyStep must beEqualTo(Cn(6))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyDivide(Ci("x"), Cn(3)).simplifyStep must beEqualTo(ApplyDivide(Ci("x"), Cn(3)))
		}
	}

	"derivative" should {
		"obey the quotient rule: (f/g)' = (f'g - g'f)/g^2" in {
			(mathml.F / G).dx must beEqualTo((Fdx * G - Gdx * F) / G ^ Cn(2))
		}
	}
}