package mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml._
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml.scalar._
import mathml.scalar.apply._
import mathml.scalar.apply.{ ApplyLn => ln }
import mathml.scalar.apply.{ ApplyLog => log }
import mathml.Match._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLCheckEqSpec extends Specification {

	"Checking equality between symbolic differentiation and manual derivative " should {

		"confirm x' = x / x " in {
			val f = x
			val g = x / x
			((f dx) ?= g) must beEqualTo(Yes)
		}

		"confirm (x^2 - x^2)' = 0 " in {
			val f = ((x ^ `2`) - (x ^ `2`))
			val g = `0`
			((f dx) ?= g) must beEqualTo(Yes)
		}

		"confirm (4*x^3*e^x)/(x^2 - 5)' =  (((12 * (x ^ 2) * (e ^ x) + 4 * (x ^ 3) * (e ^ x)) * ((x ^ 2) - 5) - (4 * (x ^ 3) * (e ^ x)) * 2 * x)) / (((x ^ 2) - 5) ^ 2) " in {
			val f = ((`4` * (x ^ `3`)) * (e ^ x)) / ((x ^ `2`) - `5`)

			val g = (((`12` * (x ^ `2`) * (e ^ x) + `4` * (x ^ `3`) * (e ^ x)) * ((x ^ `2`) - `5`) - (`4` * (x ^ `3`) * (e ^ x)) * `2` * x)) / (((x ^ `2`) - `5`) ^ `2`)

			((f dx) ?= g) must beEqualTo(Yes)
		}

		"confirm ln( 1/x + x^2 - 9)' = (-x^(-2) + 2*x)/(1/x + x^2 - 9)" in {
			val f = ln(`1` / x + (x ^ `2`) - `9`)

			val g = ((-(x ^ (`-2`))) + `2` * x) / (`1` / x + (x ^ `2`) - `9`)

			((f dx) ?= g) must beEqualTo(Yes)
		}

	}

	"Check that multiplying top and bottom of a quotient by the same thing " should {

		"have no effect for x^4 / x^2" in {
			val f = (x ^ `4`) / (x ^ `2`)
			val g = (x ^ `6`) / (x ^ `4`)
			(f ?= g) must beEqualTo(Yes)
		}

	}

}