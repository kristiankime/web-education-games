package com.artclod.mathml.scalar.apply.trig

import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import com.artclod.mathml._
import com.artclod.mathml.scalar._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplySinSpec extends Specification {

	"eval" should {
		"do sin" in {
			ApplySin(π / `2`).eval().get must beEqualTo(1)
		}
	}

	"c" should {
		"return correct sin" in {
			ApplySin(π / `2`).c.get must beEqualTo(`1`)
		}

		"fail if not a constant " in {
			ApplySin(x).c must beEmpty
		}
	}

	"s" should {
		"simplify what can be simpified" in {
			ApplySin(NeedsSimp).s must beEqualTo(ApplySin(Simplified))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplySin(x).s must beEqualTo(ApplySin(x))
		}
	}

	"d" should {
		"obey the derivative rule: sin(f)' = cos(f)f'" in {
			ApplySin(F).dx must beEqualTo(ApplyCos(F) * Fdx)
		}
	}

	"toText" should {
		"handle sin(3)" in {
			ApplySin(3).toMathJS must beEqualTo("sin(3)")
		}
	}

}





