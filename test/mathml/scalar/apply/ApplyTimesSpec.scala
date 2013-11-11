package mathml.scalar.apply

import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml._
import mathml.scalar._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove import mathml.scalar.Fdx
@RunWith(classOf[JUnitRunner])
class ApplyTimesSpec extends Specification {

	"cnStep" should {
		"return 0 if any value is zero" in {
			ApplyTimes(`1`, `0`, x).cnStep.get must beEqualTo(`0`)
		}

		"return multiplication of values if possible" in {
			ApplyTimes(`4`, `2`, `1`).cnStep.get must beEqualTo(`8`)
		}

		"return false if values do not multiply to a constant" in {
			ApplyTimes(`4`, x).isOne must beFalse
		}
	}

	"simplifyStep" should {
		"return 0 if isZero is true" in {
			ApplyTimes(`1`, `0`, `1`).simplifyStep must beEqualTo(`0`)
		}

		"return 0 if any value is zero" in {
			ApplyTimes(`1`, `0`, x).simplifyStep must beEqualTo(`0`)
		}

		"return 1 if isOne is true" in {
			ApplyTimes(`1`, `1`, `1`).simplifyStep must beEqualTo(`1`)
		}

		"multiple any constanst together" in {
			ApplyTimes(`4`, `1`, `3`).simplifyStep must beEqualTo(Cn(12))
		}

		"remove 1s in a sequence" in {
			ApplyTimes(`1`, `3`, x).simplifyStep must beEqualTo(`3` * x)
		}

		"remove 1s" in {
			ApplyTimes(`1`, x).simplifyStep must beEqualTo(x)
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyTimes(`3`, x).simplifyStep must beEqualTo(ApplyTimes(`3`, x))
		}
	}

	"derivative" should {
		"obey the product rule: (f g)' = f'g + fg'" in {
			ApplyTimes(F, G).dx must beEqualTo(Fdx * G + F * Gdx)
		}

		//		LATER
		//		"obey the multiple product rule: (f g h)' = " in {
		//			ApplyTimes(F, G, H).dx must beEqualTo(Fdx * G + F * Gdx)
		//		}
	}

}