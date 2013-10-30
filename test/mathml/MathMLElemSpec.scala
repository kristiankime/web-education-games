package mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.XML
import scala.xml.Text
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import org.specs2.matcher.Matcher
import mathml.scalar.ApplyMinusU
import mathml.scalar.ApplyDivide
import mathml.scalar.ApplyTimes
import mathml.scalar.ApplyPlus
import mathml.scalar.Plus
import mathml.scalar.ApplyMinusB
import mathml.scalar.ApplyPower
import mathml.scalar.Cn
import mathml.scalar.Ci

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLElemSpec extends Specification {

	"+" should {
		"use ApplyPlus" in {
			(F + F) must beEqualTo(ApplyPlus(F, G))
		}

		"nest when used repeatedly" in {
			(Cn(3) + Cn(4) + Cn(5)) must beEqualTo(ApplyPlus(ApplyPlus(Cn(3), Cn(4)), Cn(5)))
		}
	}

	"-" should {
		"use ApplyMinusB" in {
			(Cn(5) - Cn(3)) must beEqualTo(ApplyMinusB(Cn(5), Cn(3)))
		}

		"nest when used repeatedly" in {
			(Cn(3) - Cn(4) - Cn(5)) must beEqualTo(ApplyMinusB(ApplyMinusB(Cn(3), Cn(4)), Cn(5)))
		}
	}

	"*" should {
		"use ApplyTimes" in {
			(Cn(5) * Cn(3)) must beEqualTo(ApplyTimes(Cn(5), Cn(3)))
		}

		"nest when used repeatedly" in {
			(Cn(3) * Cn(4) * Cn(5)) must beEqualTo(ApplyTimes(ApplyTimes(Cn(3), Cn(4)), Cn(5)))
		}
	}
	
	"/" should {
		"use ApplyDivid" in {
			(Cn(5) / Cn(3)) must beEqualTo(ApplyDivide(Cn(5), Cn(3)))
		}

		"nest when used repeatedly" in {
			(Cn(3) / Cn(4) / Cn(5)) must beEqualTo(ApplyDivide(ApplyDivide(Cn(3), Cn(4)), Cn(5)))
		}
	}
	
	"^" should {
		"use ApplyPower" in {
			(Cn(5) ^ Cn(3)) must beEqualTo(ApplyPower(Cn(5), Cn(3)))
		}

		"nest when used repeatedly" in {
			(Cn(3) ^ Cn(4) ^ Cn(5)) must beEqualTo(ApplyPower(ApplyPower(Cn(3), Cn(4)), Cn(5)))
		}
	}

	"eval" should {
		"turn Cn into a number if possible" in {
			Cn(5).eval(Map()).get must beEqualTo(5)
		}

		"fail if a Cn can't be parsed into a number" in {
			Cn("not a number").isFailure must beTrue
		}

		"turn Ci into the number specified by the bound parameters" in {
			Ci("x").eval(Map("x" -> 3)).get must beEqualTo(3)
		}

		"fail if there is no entry for a Ci variable name in the bound parameters" in {
			Ci("x").eval(Map("no entry for X" -> 3)).isFailure must beTrue
		}

		"fail if there is only an applyable" in {
			Plus().eval(Map()).isFailure must beTrue
		}

		"add 2 numbers correctly for apply+plus " in {
			ApplyPlus(Cn(5), Cn(5)).eval(Map()).get must beEqualTo(10)
		}

		"add > 2 numbers correctly for apply+plus " in {
			ApplyPlus(Cn(5), Cn(5), Cn(5), Cn(5)).eval(Map()).get must beEqualTo(20)
		}

		"subtract 1 number correctly for apply+minus " in {
			ApplyMinusU(Cn(6)).eval(Map()).get must beEqualTo(-6)
		}

		"subtract 2 numbers correctly for apply+minus " in {
			ApplyMinusB(Cn(6), Cn(5)).eval(Map()).get must beEqualTo(1)
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
			Cn(3).derivative("x") must beEqualTo(Cn(0))
		}

		"derivative of the wrt variable is 1" in {
			Ci("x").derivative("x") must beEqualTo(Cn(1))
		}

		"derivative of non wrt variable is 0 (aka None)" in {
			Ci("Not X").derivative("x") must beEqualTo(Cn(0))
		}

		"sum of the derivatives is the derivative of the sums" in {
			ApplyPlus(Ci("x"), Ci("x")).derivative("x") must beEqualTo(ApplyPlus(Cn(1), Cn(1)))
		}

		"sum of the derivatives is the derivative of the sums (simplifies left None)" in {
			ApplyPlus(Cn(1), Ci("x")).derivative("x") must beEqualTo(Cn(1))
		}

		"sum of the derivatives is the derivative of the sums (simplifies right None)" in {
			ApplyPlus(Ci("x"), Cn(1)).derivative("x") must beEqualTo(Cn(1))
		}

		"sum of the derivatives is the derivative of the sums (simplifies both None)" in {
			ApplyPlus(Cn(1), Cn(1)).derivative("x") must beEqualTo(Cn(0))
		}

		"subtraction of the derivatives is the derivative of the subtractions" in {
			ApplyMinusB(Ci("x"), Ci("x")).derivative("x") must beEqualTo(Cn(0))
		}

		"subtraction of the derivatives is the derivative of the subtractions (simplifies left None)" in {
			ApplyMinusB(Cn(1), Ci("x")).derivative("x") must beEqualTo(ApplyMinusU(Cn(1)))
		}

		"subtraction of the derivatives is the derivative of the subtractions (simplifies right None)" in {
			ApplyMinusB(Ci("x"), Cn(1)).derivative("x") must beEqualTo(Cn(1))
		}

		"subtraction of the derivatives is the derivative of the subtractions (simplifies both None)" in {
			ApplyMinusB(Cn(1), Cn(1)).derivative("x") must beEqualTo(Cn(0))
		}

		"product rule" in {
			ApplyTimes(Ci("x"), Ci("x")).derivative("x") must beEqualTo(ApplyPlus(Ci("x"), Ci("x")))
		}

	}
}