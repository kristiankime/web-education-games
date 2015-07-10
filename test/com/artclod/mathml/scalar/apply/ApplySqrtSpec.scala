package com.artclod.mathml.scalar.apply

import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import com.artclod.mathml._
import com.artclod.mathml.scalar._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplySqrtSpec extends Specification {

	"eval" should {
		"do square root" in {
			ApplySqrt(4).eval().get must beEqualTo(2)
		}
	}

	"variables" should {
		"be empty if elements are constant" in {
			ApplySqrt(2).variables must beEmpty
		}

		"be x if an element constains an x" in {
			ApplySqrt(x).variables must beEqualTo(Set("x"))
		}
	}

	"c" should {
		"return correct square root" in {
			ApplySqrt(4).c.get must beEqualTo(`2`)
		}

		"fail if not a constant " in {
			ApplySqrt(x).c must beEmpty
		}
	}

	"s" should {
		"return constant if value is constant" in {
			ApplySqrt(9).s must beEqualTo(`3`)
		}

		"remain unchanged if nothing can be simplified" in {
			ApplySqrt(x).s must beEqualTo(√(x))
		}
	}

	"d" should {
		"obey the ln derivative rule: sqrt(f)' = f'/(2 * sqrt(f))" in {
			ApplySqrt(F).dx must beEqualTo(Fdx / (2 * √(F)))
		}
	}

	"toText" should {
		"handle sqrt(7)" in {
			ApplySqrt(7).toText must beEqualTo("sqrt(7)")
		}
	}
}