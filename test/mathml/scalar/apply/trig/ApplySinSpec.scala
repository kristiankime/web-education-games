package mathml.scalar.apply.trig

import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml._
import mathml.scalar._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplySinSpec extends Specification {

	"eval" should {
		"do sin" in {
			ApplySin(π / `2`).eval(Map()).get must beEqualTo(1)
		}
	}

	"variables" should {
		"be empty if elements are constant" in {
			ApplySin(`2`).variables must beEmpty
		}

		"be x if an element constains an x" in {
			ApplySin(x).variables must beEqualTo(Set("x"))
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
		"return constant if value is constant" in {
			ApplySin(π / `2`).s must beEqualTo(`1`)
		}

		"simplify what can be simpified" in {
			ApplySin(NeedsSimp).s must beEqualTo(ApplySin(Simplified))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplySin(x).s must beEqualTo(ApplySin(x))
		}

	}

	"d" should {
		"obey the sin derivative rule: ln(f)' = f'/f" in {
			ApplySin(F).dx must beEqualTo(ApplyCos(F) * Fdx)
		}
	}

}





