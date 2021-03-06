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
class ApplyTanSpec extends Specification {

	"eval" should {
		"do sin" in {
			ApplyTan(π).eval().get must beEqualTo(Trigonometry.tan(math.Pi))
		}
	}

	"c" should {
		"return correct tan" in {
			ApplyTan(π).c.get must beEqualTo(Cn(Trigonometry.tan(math.Pi)))
		}

		"fail if not a constant " in {
			ApplyTan(x).c must beEmpty
		}
	}

	"s" should {
		"simplify what can be simpified" in {
			ApplyTan(NeedsSimp).s must beEqualTo(ApplyTan(Simplified))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyTan(x).s must beEqualTo(ApplyTan(x))
		}
	}

	"d" should {
		"obey the derivative rule: sin(f)' = cos(f)f'" in {
			ApplyTan(F).dx must beEqualTo((ApplySec(F) ^ `2`) * Fdx)
		}
	}

	"toText" should {
		"handle tan(3)" in {
			ApplyTan(3).toMathJS must beEqualTo("tan(3)")
		}
	}

}





