package mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

import scala.xml.XML
import scala.xml.Text

import play.api.test._
import play.api.test.Helpers._

import org.specs2.mutable._
import org.specs2.matcher.Matcher

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLElemSpec extends Specification {

	"eval" should {

		"turn Cn into a number if possible" in {
			Cn(5).eval(Map()).get must beEqualTo(5)
		}

		"fail if a Cn can't be parsed into a number" in {
			Cn("not a number").eval(Map()) must beFailedTry
		}

		"turn Ci into the number specified by the bound parameters" in {
			Ci("X").eval(Map("X" -> 3)).get must beEqualTo(3)
		}

		"fail if there is no entry for a Ci variable name in the bound parameters" in {
			Ci("X").eval(Map("no entry for X" -> 3)) must beFailedTry
		}

		"fail if there is only an applyable" in {
			Plus().eval(Map()) must beFailedTry
		}

		"add 2 numbers correctly for apply+plus " in {
			Apply(Plus(), Cn(5), Cn(5)).eval(Map()).get must beEqualTo(10)
		}

		"add > 2 numbers correctly for apply+plus " in {
			Apply(Plus(), Cn(5), Cn(5), Cn(5), Cn(5)).eval(Map()).get must beEqualTo(20)
		}

		"subtract 2 numbers correctly for apply+minus " in {
			Apply(Minus(), Cn(6), Cn(5)).eval(Map()).get must beEqualTo(1)
		}

		"subtract > 2 numbers correctly for apply+minus " in {
			Apply(Minus(), Cn(10), Cn(4), Cn(6), Cn(3)).eval(Map()).get must beEqualTo(-3)
		}

		"multiply 2 numbers correctly for apply+times " in {
			Apply(Times(), Cn(3), Cn(-2)).eval(Map()).get must beEqualTo(-6)
		}

		"multiply > 2 numbers correctly for apply+times " in {
			Apply(Times(), Cn(-12), Cn(.5), Cn(-2), Cn(2)).eval(Map()).get must beEqualTo(24)
		}

		"divide 2 numbers correctly for apply+divide " in {
			Apply(Divide(), Cn(8), Cn(4)).eval(Map()).get must beEqualTo(2)
		}

		"divide > 2 numbers correctly for apply+divide " in {
			Apply(Divide(), Cn(12), Cn(4), Cn(3), Cn(2)).eval(Map()).get must beEqualTo(.5)
		}

		"raise a number to another numbers correctly for apply+power" in {
			Apply(Power(), Cn(3), Cn(2)).eval(Map()).get must beEqualTo(9)
		}

		"raise a number to another numbers correctly for apply+power" in {
			Apply(Power(), Cn(2), Cn(2), Cn(3)).eval(Map()).get must beEqualTo(64)
		}
		
		"nested applys work" in {
			Apply(Plus(), Cn(1), Apply(Plus(), Cn(2), Cn(3))).eval(Map()).get must beEqualTo(6)
		}
	}
}