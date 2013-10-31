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
class ApplyMinusUSpec extends Specification {

	"cnStep" should {
		"return 0 if value is 0" in {
			ApplyMinusU(`0`).cnStep.get must beEqualTo(`0`)
		}
		
		"return 1 if value is -1" in {
			ApplyMinusU(`-1`).cnStep.get must beEqualTo(`1`)
		}

		"return negative of a value" in {
			ApplyMinusU(`3`).cnStep.get must beEqualTo(`-3`)
		}

		"fail if not a constant " in {
			ApplyMinusU(x).cnStep must beNone
		}
	}

	"simplifyStep" should {
		"return constand if value is constant" in {
			ApplyMinusU(`-4`).simplifyStep must beEqualTo(`4`)
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyMinusU(x).simplifyStep must beEqualTo(ApplyMinusU(x))
		}
	}

	"derivative" should {
		"return negative of values derivative" in {
			ApplyMinusU(F).dx must beEqualTo(ApplyMinusU(Fdx))
		}
	}
	
}