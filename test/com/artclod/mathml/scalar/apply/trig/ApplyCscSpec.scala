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
class ApplyCscSpec extends Specification {

	"eval" should {
		"do csc" in {
			ApplyCsc(π/2).eval().get must beEqualTo(Trigonometry.csc(math.Pi/2))
		}
	}

	"c" should {
		"return correct cot" in {
			ApplyCsc(π/2).c.get must beEqualTo(Cn(Trigonometry.csc(math.Pi/2)))
		}

		"fail if not a constant " in {
			ApplyCsc(x).c must beEmpty
		}
	}

	"s" should {
		"simplify what can be simpified" in {
			ApplyCsc(NeedsSimp).s must beEqualTo(ApplyCsc(Simplified))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyCsc(x).s must beEqualTo(ApplyCsc(x))
		}
	}

	"d" should {
		"obey the derivative rule: csc(f)' = -cot(f) csc(f) f'" in {
			ApplyCsc(F).dx must beEqualTo((-ApplyCot(F) * ApplyCsc(F) * Fdx)s)
		}
	}

	"toText" should {
		"handle csc(3)" in {
			ApplyCsc(3).toText must beEqualTo("csc(3)")
		}
	}

}
