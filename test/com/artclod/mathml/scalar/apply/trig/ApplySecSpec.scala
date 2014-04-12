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
class ApplySecSpec extends Specification {

	"eval" should {
		"do csc" in {
			ApplySec(π).eval(Map()).get must beEqualTo(Trigonometry.sec(math.Pi))
		}
	}

	"c" should {
		"return correct sec" in {
			ApplySec(π).c.get must beEqualTo(Cn(Trigonometry.sec(math.Pi)))
		}

		"fail if not a constant " in {
			ApplySec(x).c must beEmpty
		}
	}

	"s" should {
		"simplify what can be simpified" in {
			ApplySec(NeedsSimp).s must beEqualTo(ApplySec(Simplified))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplySec(x).s must beEqualTo(ApplySec(x))
		}
	}

	"d" should {
		"obey the derivative rule: sec(f)' = tan(f) sec(f) f'" in {
			ApplySec(F).dx must beEqualTo((ApplyTan(F) * ApplySec(F) * Fdx)s)
		}
	}

}
