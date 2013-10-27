package mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.XML
import scala.xml.Text
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import org.specs2.matcher.Matcher
import mathml.scalar.ApplyPower
import mathml.scalar.Cn
import mathml.scalar.Ci

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplyPowerSpec extends Specification {

	"isZero" should {
		"return true if base is zero" in {
			ApplyPower(Cn(0), Cn(12)).isZero must beTrue
		}

		"return false if base is not zero" in {
			ApplyPower(Cn(3), Cn(2)).isZero must beFalse
		}
	}

	"isOne" should {
		"return true if base is 1" in {
			ApplyPower(Cn(1), Cn(6)).isOne must beTrue
		}

		"return true if exponent is 0" in {
			ApplyPower(Cn(12), Cn(0)).isOne must beTrue
		}

		"return false if base is not one and exp is not 0" in {
			ApplyPower(Cn(4), Cn(2)).isOne must beFalse
		}
	}

	"simplify" should {
		"return 0 if base is zero" in {
			ApplyPower(Cn(0), Cn(5)).simplify must beEqualTo(Cn(0))
		}

		"return 1 if base is 1" in {
			ApplyPower(Cn(1), Cn(12)).simplify must beEqualTo(Cn(1))
		}

		"return 1 if exponent is 0" in {
			ApplyPower(Cn(4), Cn(0)).simplify must beEqualTo(Cn(1))
		}
	}

	"derivative" should {
		"obey the elementary power rule: (x^n)' = n*x^(n-1)" in {
			(Ci("x") ^ Cn(3)).dx must beEqualTo(Cn(3) * Ci("x") ^ (Cn(3) - Cn(1)))
		}

		"obey the chain power rule: (f^n)' = n*f^(n-1)f'" in {
			(F ^ Cn(3)).dx must beEqualTo(Cn(3) * F ^ (Cn(3) - Cn(1)) * Fdx)
		}

		// LATER get the Generalized power rule working
		// (f(x)^(g(x)))' = f(x)^(g(x)-1)*(g(x)*'(x)+f(x)*log(f(x))*g'(x))
	}
}