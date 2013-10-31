package mathml.scalar

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml._
import mathml.scalar._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplyDivideSpec extends Specification {

	"variables" should {
		"be empty if element is constant" in {
			ApplyDivide(`1`, `2`).variables must beEmpty
		}

		"be x if element constains an x" in {
			ApplyDivide(x, `2`).variables must beEqualTo(Set("x"))
		}

		"be y if element constains a y" in {
			ApplyDivide(y, `2`).variables must beEqualTo(Set("y"))
		}
		
		"be x &y if element constains x & y" in {
			ApplyDivide(x, y).variables must beEqualTo(Set("x", "y"))
		}
	}

	"cnStep" should {
		"return correct division if numerator and denominator are numbers " in {
			ApplyDivide(`6`, `4`).cnStep.get must beEqualTo(`1.5`)
		}

		"return 0 if numerator is 0 " in {
			ApplyDivide(`0`, `4`).cnStep.get must beEqualTo(`0`)
		}

		"return 1 if numerator and denominator are equal" in {
			ApplyDivide(`5`, `5`).cnStep.get must beEqualTo(`1`)
		}

		"fail if not a constant " in {
			ApplyDivide(x, `4`).cnStep must beEmpty
		}
	}

	"simplifyStep" should {
		"return 0 if numerator is 0 (and denominator is not)" in {
			ApplyDivide(`0`, `6`).simplifyStep must beEqualTo(`0`)
		}

		"return 1 if numerator and denominator are equal (and non zero)" in {
			ApplyDivide(`4`, `4`).simplifyStep must beEqualTo(`1`)
		}

		"return numerator if denominator is 1" in {
			ApplyDivide(x, `1`).simplifyStep must beEqualTo(x)
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyDivide(x, `3`).simplifyStep must beEqualTo(ApplyDivide(x, `3`))
		}
	}

	"derivative" should {
		"obey the quotient rule: (f/g)' = (f'g - g'f)/g^2" in {
			ApplyDivide(F, G).dx must beEqualTo((Fdx * G - Gdx * F) / (G ^ `2`))
		}
	}

}