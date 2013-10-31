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
class ApplyPlusSpec extends Specification {

	"cnStep" should {
		"return sum of elements if all elements are constants" in {
			ApplyPlus(`2`, `3`, Cn(-1)).cnStep.get must beEqualTo(`4`)
		}

		"return None if any elements are not constant" in {
			ApplyPlus(`2`, x, `1`).cnStep must beNone
		}
	}

	"simplifyStep" should {
		"return 0 if all values are 0" in {
			ApplyPlus(`0`, `0`, `0`).simplifyStep must beEqualTo(`0`)
		}

		"return 1 if exactly one value is 1" in {
			ApplyPlus(`0`, `1`, `0`).simplifyStep must beEqualTo(`1`)
		}

		"sum values if they are all constant" in {
			ApplyPlus(`4`, `0`, `1`, `3`, `0`).simplifyStep must beEqualTo(`8`)
		}

		"sum constants and leave variables (constands go to end)" in {
			ApplyPlus(x, `3`, `4`, y).simplifyStep must beEqualTo(ApplyPlus(x, y, `7`))
		}
		
		"remain unchanged if nothing can be simplified" in {
			ApplyPlus(x, `3`).simplifyStep must beEqualTo(ApplyPlus(x, `3`))
		}
	}

	"derivative" should {
		"obey the sum rule: (f + g)' = f' + g'" in {
			ApplyPlus(F, G).dx must beEqualTo(ApplyPlus(Fdx, Gdx))
		}
		
		"obey the sum rule for more than 2 elements: (f + g + h)' = f' + g' + h'" in {
			ApplyPlus(F, G, H).dx must beEqualTo(ApplyPlus(Fdx, Gdx, Hdx))
		}
	}
	
}