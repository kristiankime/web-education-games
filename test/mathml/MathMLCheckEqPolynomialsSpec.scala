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

		"confirm ln(x)' = 1 / x" in {
			val f = ln(x) dx
			val g = `1` / x
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm 1 / ln(x)' = -1 / (x * log(x)^2)" in {
			val f = (`1` / ln(x)) dx
			val g = `-1` / (x * (ln(x) ^ `2`))
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm x / ln(x)' = (ln(x)-1) / (ln(x)^2)" in {
			val f = (x / ln(x)) dx
			val g = (ln(x) - `1`) / (ln(x) ^ `2`)
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm (x/(x+ln(x)))' = (ln(x)-1) / (x+ln(x))^2" in {
			val f = x / (x + ln(x)) dx
			val g = (ln(x) - `1`) / ((x + ln(x)) ^ `2`)
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm (x/(x+log(x)))' = (ln(10)*(ln(x) - 1))/(x*ln(10)+ln(x))^2" in {
			val f = (x / (x + log(x))) dx
			val g = (ln(`10`) * (ln(x) - `1`)) / ((x * ln(`10`) + ln(x)) ^ `2`)
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm x' = x / x " in {
			val f = x dx
			val g = x / x
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm (e ^ x)' = e ^ x" in {
			val f = (e ^ x) dx
			val g = e ^ x
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm (e ^ 10*x)' = 10 * e ^ 10*x" in {
			val f = (e ^ (`10` * x)) dx
			val g = `10` * (e ^ (`10` * x))
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm e ^ 10*x' != 11* e ^ 10*x" in {
			val f = (e ^ (`10` * x)) dx
			val g = `11` * (e ^ (`10` * x))
			(f ?= g) must beEqualTo(No)
		}

		"confirm e ^ 100*x' = 100 * e ^ 100*x" in {
			val f = (e ^ (`100` * x)) dx
			val g = `100` * (e ^ (`100` * x))
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm e ^ 100*x' != 101* e ^ 100*x" in {
			val f = (e ^ (`100` * x)) dx
			val g = `101` * (e ^ (`100` * x))
			(f ?= g) must beEqualTo(No)
		}

		"confirm e ^ 1000*x' = 1000 * e ^ 1000*x" in {
			val f = (e ^ (`1000` * x)) dx
			val g = `1000` * (e ^ (`1000` * x))
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm e ^ 1000*x' != 1001* e ^ 1000*x" in {
			val f = (e ^ (`1000` * x)) dx
			val g = `1001` * (e ^ (`1000` * x))
			(f ?= g) must beEqualTo(No)
		}

		"confirm e ^ -100*x' = -100 * e ^ -100*x" in {
			val f = (e ^ (`-100` * x)) dx
			val g = `-100` * (e ^ (`-100` * x))
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm e ^ -100*x' = -101 * e ^ -100*x" in {
			val f = (e ^ (`-100` * x)) dx
			val g = `-101` * (e ^ (`-100` * x))
			(f ?= g) must beEqualTo(No)
		}
	}

}