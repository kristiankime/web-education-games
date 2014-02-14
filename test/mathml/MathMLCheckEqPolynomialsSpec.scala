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
class MathMLCheckEqPolynomialsSpec extends Specification {

	"Checking equality between symbolic differentiation and manual derivative for these polynomials" should {

		"confirm (5x + 4)' = 5" in {
			val f = (`5` * x + `4`) dx
			val g = `5`
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm (5x + 4)' != 4.9" in {
			val f = (`5` * x + `4`) dx
			val g = Cn(4.9)
			(f ?= g) must beEqualTo(No)
		}

		"confirm (x^3)' = 3x^2" in {
			val f = (x ^ `3`)dx
			val g = `3` * (x ^ `2`)
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm (x^3)' != 4x^2" in {
			val f = (x ^ `3`) dx
			val g = `4` * (x ^ `2`)
			(f ?= g) must beEqualTo(No)
		}

		"confirm (2x^2 + -3x + -2)' = 4x -3" in {
			val f = (`2` * (x ^ `2`) + `-3` * x + `-2`) dx
			val g = (`4` * x - `3`)
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm (2x^2 + -3x + -2)' != 3x -3" in {
			val f = (`2` * (x ^ `2`) + `-3` * x + `-2`) dx
			val g = (`3` * x - `3`)
			(f ?= g) must beEqualTo(No)
		}

		"confirm (x^3 + 3x + 4)' = 3x^2+3" in {
			val f = ((x ^ `3`) + `3` * x + `4`) dx
			val g = (`3` * (x ^ `2`) + `3`)
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm (x^3 + 3x) = 3x^2 + 3" in {
			val f = ((x ^ `3`) + `3` * x) dx
			val g = (`3` * (x ^ `2`) + `3`)
			(f ?= g) must beEqualTo(Yes)
		}

	}

}