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

	"Checking equality between symbolic differntiaton and manual derivative " should {

		"work for simple exponential (x^3)" in {
			val f = x ^ `3`
			val d = f.dx
			MathML.checkEq("x", d, `3` * (x ^ `2`)) must beEqualTo(Yes)
		}

		"work for degree 1 polynomial (5 * x + 4)" in {
			val f = (`5` * x + `4`)
			val d = f.dx
			MathML.checkEq("x", d, `5`) must beEqualTo(Yes)
		}

		"work for degree 2 polynomial (2 * x ^ 2 + -3 * x + -2)" in {
			val f = (`2` * (x ^ `2`) + `-3` * x + `-2`)
			val d = f.dx
			MathML.checkEq("x", d, (`4` * x + `-3`)) must beEqualTo(Yes)
		}

		"work for degree 3 polynomial (x^3 + 3x + 4)" in {
			val f = ((x ^ `3`) + `3` * x + `4`)
			val d = f.dx
			MathML.checkEq("x", d, (`3` * (x ^ `2`)) + `3`) must beEqualTo(Yes)
		}

		"work for degree 3 polynomial (x^3 + 3x)" in {
			val f = ((x ^ `3`) + `3` * x)
			val d = f.dx
			MathML.checkEq("x", d, (`3` * (x ^ `2`)) + `3`) must beEqualTo(Yes)
		}

		"ln(x)' = 1 / x" in {
			val f = ln(x) dx
			val g = `1` / x
			(f ?= g) must beEqualTo(Yes)
		}

		"1 / ln(x)' = -1 / (x * log(x)^2)" in {
			val f = (`1` / ln(x)) dx
			val g = `-1` / (x * (ln(x) ^ `2`))
			(f ?= g) must beEqualTo(Yes)
		}

		"x / ln(x)' = (ln(x)-1) / (ln(x)^2)" in {
			val f = (x / ln(x)) dx
			val g = (ln(x) - `1`) / (ln(x) ^ `2`)
			(f ?= g) must beEqualTo(Yes)
		}

		"(x/(x+ln(x)))' = (ln(x)-1) / (x+ln(x))^2" in {
			val f = x / (x + ln(x)) dx
			val g = (ln(x) - `1`) / ((x + ln(x)) ^ `2`)
			(f ?= g) must beEqualTo(Yes)
		}

		"(x/(x+log(x)))' = (ln(10)*(ln(x) - 1))/(x*ln(10)+ln(x))^2" in {
			val f = (x / (x + log(x))) dx
			val g = (ln(`10`) * (ln(x) - `1`)) / ((x * ln(`10`) + ln(x)) ^ `2`)
			(f ?= g) must beEqualTo(Yes)
		}

		"x' = x / x " in {
			val f = x dx
			val g = x / x
			(f ?= g) must beEqualTo(Yes)
		}
		
		"e ^ x' = e ^ x" in {
			val f = (e ^ x) dx
			val g = e ^ x
			(f ?= g) must beEqualTo(Yes)
		}
		
		"e ^ 10*x' = 10 * e ^ 10*x" in {
			val f = (e ^ (`10` * x)) dx
			val g = `10` * (e ^ (`10` * x))
			(f ?= g) must beEqualTo(Yes)
		}
		
		"e ^ 10*x' != 11* e ^ 10*x" in {
			val f = (e ^ (`10` * x)) dx
			val g = `11` * (e ^ (`10` * x))
			(f ?= g) must beEqualTo(No)
		}
		
		"e ^ 100*x' = 100 * e ^ 100*x" in {
			val f = (e ^ (`100` * x)) dx
			val g = `100` * (e ^ (`100` * x))
			(f ?= g) must beEqualTo(Yes)
		}
		
		"e ^ 100*x' != 101* e ^ 100*x" in {
			val f = (e ^ (`100` * x)) dx
			val g = `101` * (e ^ (`100` * x))
			(f ?= g) must beEqualTo(No)
		}
		
		"e ^ 1000*x' = 1000 * e ^ 1000*x" in {
			val f = (e ^ (`1000` * x)) dx
			val g = `1000` * (e ^ (`1000` * x))
			(f ?= g) must beEqualTo(Yes)
		}
		
		"e ^ 1000*x' != 1001* e ^ 1000*x" in {
			val f = (e ^ (`1000` * x)) dx
			val g = `1001` * (e ^ (`1000` * x))
			(f ?= g) must beEqualTo(No)
		}
		
		"e ^ -100*x' = -100 * e ^ -100*x" in {
			val f = (e ^ (`-100` * x)) dx
			val g = `-100` * (e ^ (`-100` * x))
			(f ?= g) must beEqualTo(Yes)
		}
		
		"e ^ -100*x' = -101 * e ^ -100*x" in {
			val f = (e ^ (`-100` * x)) dx
			val g = `-101` * (e ^ (`-100` * x))
			(f ?= g) must beEqualTo(No)
		}
	}

}