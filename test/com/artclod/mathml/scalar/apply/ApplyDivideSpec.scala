package com.artclod.mathml.scalar.apply

import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import com.artclod.mathml._
import com.artclod.mathml.scalar._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplyDivideSpec extends Specification {

	"eval" should {
		"do division" in {
			ApplyDivide(`1`, `2`).eval().get must beEqualTo(.5)
		}

		"be zero if numerator is zero" in {
			ApplyDivide(`0`, `2`).eval().get must beEqualTo(0)
		}

		"be failure double divisions evals to zero but numerator is not zero" in {
			ApplyDivide(Cn(1E-300), Cn(1E+300)).eval() must beFailedTry
		}
	}

	"variables" should {
		"be empty if elements are constant" in {
			ApplyDivide(`1`, `2`).variables must beEmpty
		}

		"be x if an element constains an x" in {
			ApplyDivide(x, `2`).variables must beEqualTo(Set("x"))
		}

		"be y if element constains a y" in {
			ApplyDivide(`1`, y).variables must beEqualTo(Set("y"))
		}

		"be x & y if element constains x & y" in {
			ApplyDivide(x, y).variables must beEqualTo(Set("x", "y"))
		}
	}

	"c" should {
		"return correct division if numerator and denominator are numbers " in {
			ApplyDivide(`6`, `4`).c.get must beEqualTo(`1.5`)
		}

		"return 0 if numerator is 0 " in {
			ApplyDivide(`0`, `4`).c.get must beEqualTo(`0`)
		}

		"return 1 if numerator and denominator are equal" in {
			ApplyDivide(`5`, `5`).c.get must beEqualTo(`1`)
		}

		"fail if not a constant " in {
			ApplyDivide(x, `4`).c must beEmpty
		}
	}

	"s" should {
		"return 0 if numerator is 0 (and denominator is not)" in {
			ApplyDivide(`0`, `6`).s must beEqualTo(`0`)
		}

		"return 1 if numerator and denominator are equal (and non zero)" in {
			ApplyDivide(`4`, `4`).s must beEqualTo(`1`)
		}

		"return numerator if denominator is 1" in {
			ApplyDivide(x, `1`).s must beEqualTo(x)
		}

		"simplify if numerator is also a divide" in {
			ApplyDivide(x / `2`, y).s must beEqualTo(x / (`2` * y))
		}

		"simplify if denominator is also a divide" in {
			ApplyDivide(x, y / `3`).s must beEqualTo((`3` * x) / y)
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyDivide(x, `3`).s must beEqualTo(ApplyDivide(x, `3`))
		}
	}

	"d" should {
		"obey the quotient rule: (f/g)' = (f'g - g'f)/g^2" in {
			ApplyDivide(F, G).dx must beEqualTo((Fdx * G - Gdx * F) / (G ^ `2`))
		}
	}

	"toText" should {
		"handle 3 / 7" in {
			ApplyDivide(3, 7).toMathJS must beEqualTo("(3 / 7)")
		}
	}

}