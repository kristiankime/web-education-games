package com.artclod.mathml.scalar.apply

import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import com.artclod.mathml._
import com.artclod.mathml.scalar._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import math.E

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplyRootSpec extends Specification {

	"eval" should {
		"do nth root" in {
			ApplyRoot(3, 8).eval().get must beEqualTo(2)
		}
	}

	"variables" should {
		"be empty if elements are constant" in {
			ApplyRoot(3, 8).variables must beEmpty
		}

		"be x if an element constains an x" in {
			ApplyRoot(5, x).variables must beEqualTo(Set("x"))
		}
	}

	"c" should {
		"return correct log" in {
			ApplyRoot(3, 8).c.get must beEqualTo(`2`)
		}

		"fail if not a constant " in {
			ApplyRoot(5, x).c must beEmpty
		}
	}

	"s" should {
		"return constant if value is constant" in {
			ApplyRoot(4, 16).s must beEqualTo(`2`)
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyRoot(5, x).s must beEqualTo(ApplyRoot(5, x))
		}
	}

	"d" should {
		"obey the derivative rule for sqrt" in {
			ApplyRoot(2, F).dx must beEqualTo(Fdx / (2 * √(F)))
		}

		"obey the derivative rule for arbitrary degree" in {
			ApplyRoot(5, F).dx must beEqualTo(Fdx / (5 * `n√`(5d / (5-1))(F)))
		}
	}

}