package com.artclod.mathml.scalar.apply.trig

import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import com.artclod.mathml._
import com.artclod.mathml.scalar._
import com.artclod.mathml.scalar.concept.Trigonometry

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplyCotSpec extends Specification {

	"eval" should {
		"do cot" in {
			ApplyCot(π).eval(Map()).get must beEqualTo(Trigonometry.cot(math.Pi))
		}
	}

	"c" should {
		"return correct cot" in {
			ApplyCot(π).c.get must beEqualTo(Cn(Trigonometry.cot(math.Pi)))
		}

		"fail if not a constant " in {
			ApplyCot(x).c must beEmpty
		}
	}

	"s" should {
		"simplify what can be simpified" in {
			ApplyCot(NeedsSimp).s must beEqualTo(ApplyCot(Simplified))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyCot(x).s must beEqualTo(ApplyCot(x))
		}
	}

	"d" should {
		"obey the derivative rule: cot(f)' = -csc(f)^2 * f'" in {
			ApplyCot(F).dx must beEqualTo((-(ApplyCsc(F) ^ `2`)) * Fdx)
		}
	}

}
