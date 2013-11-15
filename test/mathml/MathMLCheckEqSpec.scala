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

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLCheckEqSpec extends Specification {

	"Checking equality between symbolic differntiaton and manual derivative " should {

		"work for simple exponential (x^3)" in {
			val f = x ^ `3`
			val d = f.dx
			MathML.checkEq("x", d, `3` * (x ^ `2`)) must beTrue
		}

		"work for degree 1 polynomial (5 * x + 4)" in {
			val f = (`5` * x + `4`)
			val d = f.dx
			MathML.checkEq("x", d, `5`) must beTrue
		}

		"work for degree 2 polynomial (2 * x ^ 2 + -3 * x + -2)" in {
			val f = (`2` * (x ^ `2`) + `-3` * x + `-2`)
			val d = f.dx
			MathML.checkEq("x", d, (`4` * x + `-3`)) must beTrue
		}

		"work for degree 3 polynomial (x^3 + 3x + 4)" in {
			val f = ((x ^ `3`) + `3` * x + `4`)
			val d = f.dx
			MathML.checkEq("x", d, (`3` * (x ^ `2`)) + `3`) must beTrue
		}

		"work for degree 3 polynomial (x^3 + 3x)" in {
			val f = ((x ^ `3`) + `3` * x)
			val d = f.dx
			MathML.checkEq("x", d, (`3` * (x ^ `2`)) + `3`) must beTrue
		}

		"ln(x)' = 1 / x" in {
			val f = ln(x)ʹ
			val g = `1` / x
			(f ?= g) must beTrue
		}

		"1 / ln(x)' = -1 / (x * log(x)^2)" in {
			val f = (`1` / ln(x))ʹ
			val g = `-1` / (x * (ln(x) ^ `2`))
			(f ?= g) must beTrue
		}

		"x / ln(x)' = (ln(x)-1) / (ln(x)^2)" in {
			val f = (x / ln(x))ʹ
			val g = (ln(x) - `1`) / (ln(x) ^ `2`)
			(f ?= g) must beTrue
		}

		"(x/(x+ln(x)))' = (ln(x)-1) / (x+ln(x))^2" in {
			val f = x / (x + ln(x))ʹ
			val g = (ln(x) - `1`) / ((x + ln(x)) ^ `2`)
			(f ?= g) must beTrue
		}

	}

}