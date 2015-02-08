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
class ApplyLog10Spec extends Specification {

	"eval" should {
		"do natural log" in {
			ApplyLog10(Cn(100)).eval().get must beEqualTo(2)
		}
	}

	"variables" should {
		"be empty if elements are constant" in {
			ApplyLog10(`2`).variables must beEmpty
		}

		"be x if an element constains an x" in {
			ApplyLog10(x).variables must beEqualTo(Set("x"))
		}
	}

	"c" should {
		"return correct log" in {
			ApplyLog10(Cn(math.pow(10, 2))).c.get must beEqualTo(Cn(2))
		}

		"fail if not a constant " in {
			ApplyLog10(x).c must beEmpty
		}
	}

	"s" should {
		"return constant if value is constant" in {
			ApplyLog10(Cn(math.pow(10, 3.5))).s must beEqualTo(`3.5`)
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyLog10(x).s must beEqualTo(ApplyLog10(x))
		}
	}

	"d" should {
		"obey the ln derivative rule: log_10(f)' = f' / ln(10) f" in {
			ApplyLog10(F).dx must beEqualTo(Fdx / (ln_10 * F))
		}
	}

}