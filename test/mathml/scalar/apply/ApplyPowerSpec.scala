package mathml.scalar.apply

import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml._
import mathml.scalar._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

// LATER try out http://rlegendi.github.io/specs2/runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplyPowerSpec extends Specification {

	"cnStep" should {
		"return 0 if base is 0" in {
			ApplyPower(`0`, x).cnStep.get must beEqualTo(`0`)
		}

		"return 1 if base is 1" in {
			ApplyPower(`1`, x).cnStep.get must beEqualTo(`1`)
		}

		"return base if power is 1" in {
			ApplyPower(`5`, `1`).cnStep.get must beEqualTo(`5`)
		}

		"return 1 if power is 0" in {
			ApplyPower(x, `0`).cnStep.get must beEqualTo(`1`)
		}

		"return None if function is not constant" in {
			ApplyPower(`2`, x).cnStep must beNone
		}
	}

	"simplifyStep" should {
		"return 0 if base is zero" in {
			ApplyPower(`0`, x).simplifyStep must beEqualTo(`0`)
		}

		"return 1 if base is 1" in {
			ApplyPower(`1`, x).simplifyStep must beEqualTo(`1`)
		}

		"return 1 if exponent is 0" in {
			ApplyPower(x, `0`).simplifyStep must beEqualTo(`1`)
		}

		"return base if exponent is 1" in {
			ApplyPower(x, `1`).simplifyStep must beEqualTo(x)
		}
	}

	"derivative" should {

		"obey the elementary power rule: (x^n)' = n*x^(n-1)" in {
			ApplyPower(x, `3`).dx must beEqualTo(`3` * (x ^ `2`))
		}

		"obey the chain power rule: (f^n)' = n*f^(n-1)f'" in {
			ApplyPower(F, `3`).dx must beEqualTo(ApplyTimes(`3`, F ^ `2`, Fdx))
		}

		// LATER get the Generalized power rule working
		// (f(x)^(g(x)))' = f(x)^(g(x)-1)*(g(x)*'(x)+f(x)*log(f(x))*g'(x))
	}
}