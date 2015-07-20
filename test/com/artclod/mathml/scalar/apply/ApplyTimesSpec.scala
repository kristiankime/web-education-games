package com.artclod.mathml.scalar.apply

import org.specs2.runner.JUnitRunner
import org.specs2.mutable._
import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import com.artclod.mathml._
import com.artclod.mathml.scalar._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove import com.artclod.mathml.scalar.Fdx
@RunWith(classOf[JUnitRunner])
class ApplyTimesSpec extends Specification {

	"eval" should {
		"multiply two numbers" in {
			ApplyTimes(`3`, `-2`).eval().get must beEqualTo(-6)
		}

		"multiply many numbers" in {
			ApplyTimes(`3`, `2`, `4`).eval().get must beEqualTo(24)
		}
		
		"return 0 if any numbers are 0" in {
			ApplyTimes(`.5`, `2`, `0`).eval().get must beEqualTo(0)
		}
		
		"fail if non zero numbers produce a 0 output" in {
			ApplyTimes(Cn(1E-300), Cn(1E-300)).eval() must beFailedTry
		}
	}

	"variables" should {
		"be empty if element is constant" in {
			ApplyTimes(`1`, `2`).variables must beEmpty
		}

		"be x if element constains an x" in {
			ApplyTimes(x, `2`).variables must beEqualTo(Set("x"))
		}

		"be y if element constains a y" in {
			ApplyTimes(y, `2`).variables must beEqualTo(Set("y"))
		}

		"be x & y if element constains x & y" in {
			ApplyTimes(x, y).variables must beEqualTo(Set("x", "y"))
		}
	}

	"c" should {
		"return 0 if any value is zero" in {
			ApplyTimes(`1`, `0`, x).c.get must beEqualTo(`0`)
		}

		"return multiplication of values if possible" in {
			ApplyTimes(`4`, `2`, `1`).c.get must beEqualTo(`8`)
		}

		"return none if values do not multiply to a constant" in {
			ApplyTimes(`4`, x).c.isEmpty must beTrue
		}
	}

	"s" should {
		"return 0 if isZero is true" in {
			ApplyTimes(`1`, `0`, `1`).s must beEqualTo(`0`)
		}

		"return 0 if any value is zero" in {
			ApplyTimes(`1`, `0`, x).s must beEqualTo(`0`)
		}

		"return 1 if isOne is true" in {
			ApplyTimes(`1`, `1`, `1`).s must beEqualTo(`1`)
		}

		"multiple any constanst together" in {
			ApplyTimes(`4`, `1`, `3`).s must beEqualTo(Cn(12))
		}

		"remove 1s in a sequence" in {
			ApplyTimes(`1`, `3`, x).s must beEqualTo(`3` * x)
		}

		"remove 1s" in {
			ApplyTimes(`1`, x).s must beEqualTo(x)
		}

		"multiply constants and leave variables, with nested elements  (constands go to end)" in {
			ApplyTimes(x, `4`, y, (`2` * `3`)).s must beEqualTo(ApplyTimes(`24`, x, y))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyTimes(`3`, x).s must beEqualTo(ApplyTimes(`3`, x))
		}
	}

	"d" should {
		"obey the product rule: (f g)' = f'g + fg'" in {
			ApplyTimes(F, G).dx must beEqualTo(Fdx * G + F * Gdx)
		}

		// (f(x) g(x) h(x))' = g(x)h(x)f'(x) + f(x)h(x)g'(x) + f(x)g(x)h'(x)
		"obey the multiple product rule: (fgh)' = f'gh + fg'h + fgh'" in {
			ApplyTimes(F, G, H).dx must beEqualTo(ApplyPlus(ApplyTimes(Fdx, G, H), ApplyTimes(F, Gdx, H), ApplyTimes(F, G, Hdx)))
		}
	}

	"toText" should {
		"handle 3 * 5" in {
			ApplyTimes(3, 5).toMathJS must beEqualTo("(3 * 5)")
		}
	}

}