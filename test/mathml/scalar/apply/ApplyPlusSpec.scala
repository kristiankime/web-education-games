package mathml.scalar.apply

import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml._
import mathml.scalar._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplyPlusSpec extends Specification {

	"c" should {
		"return sum of elements if all elements are constants" in {
			ApplyPlus(`2`, `3`, `-1`).c.get must beEqualTo(`4`)
		}
		
		"return sum of nested elements if all elements are constants" in {
			ApplyPlus(`2`, `1`, (`3` + `4`), `-1`).c.get must beEqualTo(`9`)
		}

		"return None if any elements are not constant" in {
			ApplyPlus(`2`, x, `1`).c must beNone
		}
	}

	"s" should {
		"return 0 if all values are 0" in {
			ApplyPlus(`0`, `0`, `0`).s must beEqualTo(`0`)
		}

		"return 1 if exactly one value is 1" in {
			ApplyPlus(`0`, `1`, `0`).s must beEqualTo(`1`)
		}

		"sum values if they are all constant" in {
			ApplyPlus(`4`, `0`, `1`, `3`, `0`).s must beEqualTo(`8`)
		}

		"sum constants and leave variables (constants go to end)" in {
			ApplyPlus(x, `3`, `4`, y).s must beEqualTo(ApplyPlus(x, y, `7`))
		}
		
		"sum constants and leave variables, drop constants if they sum to 0" in {
			ApplyPlus(x, `-3`, `3`).s must beEqualTo(x)
		}
		
		"sum constants and leave variables, with nested elements  (constands go to end)" in {
			ApplyPlus((x + `3`), `4`, y, (`2` + `3`)).s must beEqualTo(ApplyPlus(x, y, `12`))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyPlus(x, `3`).s must beEqualTo(ApplyPlus(x, `3`))
		}
	}

	"derivative" should {
		"obey the sum rule: (f + g)' = f' + g'" in {
			ApplyPlus(F, G).dx must beEqualTo(Fdx + Gdx)
		}

		"obey the sum rule for more than 2 elements: (f + g + h)' = f' + g' + h'" in {
			ApplyPlus(F, G, H).dx must beEqualTo(ApplyPlus(Fdx, Gdx, Hdx))
		}
	}

}