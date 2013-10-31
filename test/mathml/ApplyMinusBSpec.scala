package mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.XML
import scala.xml.Text
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import org.specs2.matcher.Matcher
import mathml.scalar._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplyMinusBSpec extends Specification {

	"cnStep" should {
		"return subtraction if values are numbers " in {
			ApplyMinusB(`6`, `4`).cnStep.get must beEqualTo(`2`)
		}

		"return 0 if values are equal " in {
			ApplyMinusB(`5`, `5`).cnStep.get must beEqualTo(`0`)
		}

		"fail if not a constant " in {
			ApplyMinusB(x, `5`).cnStep must beNone
		}
	}

	"simplifyStep" should {
		"return 0 if numbers are the same" in {
			ApplyMinusB(`1`, `1`).simplifyStep must beEqualTo(`0`)
		}

		"return 1 if numbers subtract to 1" in {
			ApplyMinusB(`3`, `2`).simplifyStep must beEqualTo(`1`)
		}

		"return first value if second value is 0" in {
			ApplyMinusB(Cn(4), `0`).simplifyStep must beEqualTo(`4`)
		}

		"return minus second value if first value is 0" in {
			ApplyMinusB(`0`, `3`).simplifyStep must beEqualTo(`-3`)
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyMinusB(`3`, x).simplifyStep must beEqualTo(`3` - x)
		}
	}

	"derivative" should {
		"obey the subtraction rule: (f - g)' = f' - g'" in {
			ApplyMinusB(F, G).dx must beEqualTo(Fdx - Gdx)
		}
	}
	
}