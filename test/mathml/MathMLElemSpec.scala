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
//			MathML(<cn>not a number</cn>).get.eval(Map()) must beFailedTry
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
			ApplyPlus(Cn(5), Cn(5)).eval(Map()).get must beEqualTo(10)
		}

		"add > 2 numbers correctly for apply+plus " in {
			ApplyPlus(Cn(5), Cn(5), Cn(5), Cn(5)).eval(Map()).get must beEqualTo(20)
		}

		"subtract 1 number correctly for apply+minus " in {
			ApplyMinusUnary(Cn(6)).eval(Map()).get must beEqualTo(-6)
		}
		
		"subtract 2 numbers correctly for apply+minus " in {
			ApplyMinusBinary(Cn(6), Cn(5)).eval(Map()).get must beEqualTo(1)
		}

		"multiply 2 numbers correctly for apply+times " in {
			ApplyTimes(Cn(3), Cn(-2)).eval(Map()).get must beEqualTo(-6)
		}

		"multiply > 2 numbers correctly for apply+times " in {
			ApplyTimes(Cn(-12), Cn(.5), Cn(-2), Cn(2)).eval(Map()).get must beEqualTo(24)
		}

		"divide 2 numbers correctly for apply+divide " in {
			ApplyDivide(Cn(8), Cn(4)).eval(Map()).get must beEqualTo(2)
		}

		"raise a number to another numbers correctly for apply+power" in {
			ApplyPower(Cn(3), Cn(2)).eval(Map()).get must beEqualTo(9)
		}
		
		"nested applys work" in {
			ApplyPlus(Cn(1), ApplyPlus(Cn(2), Cn(3))).eval(Map()).get must beEqualTo(6)
		}
	}
	
	"derivative" should {
		"derivative of a constant is 0 (aka None)" in {
			Cn(3).derivative("X") must beNone
		}
		
		"derivative of the wrt variable is 1" in {
			Ci("X").derivative("X").get must beEqualTo(Cn(1))
		}

		"derivative of non wrt variable is 0 (aka None)" in {
			Ci("Not X").derivative("X") must beNone
		}

		"sum of the derivatives is the derivative of the sums" in {
			ApplyPlus(Ci("X"), Ci("X")).derivative("X").get must beEqualTo(ApplyPlus(Cn(1), Cn(1)))
		}
		
		"sum of the derivatives is the derivative of the sums (simplifies left None)" in {
			ApplyPlus(Cn(1), Ci("X")).derivative("X").get must beEqualTo(Cn(1))
		}
		
		"sum of the derivatives is the derivative of the sums (simplifies right None)" in {
			ApplyPlus(Ci("X"), Cn(1)).derivative("X").get must beEqualTo(Cn(1))
		}
		
		"sum of the derivatives is the derivative of the sums (simplifies both None)" in {
			ApplyPlus(Cn(1), Cn(1)).derivative("X") must beNone
		}
		
		"subtraction of the derivatives is the derivative of the subtractions" in {
			ApplyMinusBinary(Ci("X"), Ci("X")).derivative("X").get must beEqualTo(ApplyMinusBinary(Cn(1), Cn(1)))
		}
		
		"subtraction of the derivatives is the derivative of the subtractions (simplifies left None)" in {
			ApplyMinusBinary(Cn(1), Ci("X")).derivative("X").get must beEqualTo(ApplyMinusUnary(Cn(1)))
		}
		
		"subtraction of the derivatives is the derivative of the subtractions (simplifies right None)" in {
			ApplyMinusBinary(Ci("X"), Cn(1)).derivative("X").get must beEqualTo(Cn(1))
		}
		
		"subtraction of the derivatives is the derivative of the subtractions (simplifies both None)" in {
			ApplyMinusBinary(Cn(1), Cn(1)).derivative("X") must beNone
		}
	}
}