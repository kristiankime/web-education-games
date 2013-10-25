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
class ApplyMinusBSpec extends Specification {

	"isZero" should {
		"return true if two values are equal" in {
			ApplyMinusB(Cn(1), Cn(1)).isZero must beTrue
		}

		"return false if two values are different" in {
			ApplyMinusB(Cn(1), Cn(2)).isZero must beFalse
		}
	}

	"isOne" should {
		"return true if first value is 1 and second value is 0" in {
			ApplyMinusB(Cn(1), Cn(0)).isOne must beTrue
		}

		"return false if values are not equal" in {
			ApplyMinusB(Cn(4), Cn(2)).isOne must beFalse
		}
	}

	"simplify" should {
		"return 0 if isZero is true" in {
			ApplyMinusB(Cn(1), Cn(1)).simplify must beEqualTo(Cn(0))
		}

		"return 1 if isOne is true" in {
			ApplyMinusB(Cn(1), Cn(0)).simplify must beEqualTo(Cn(1))
		}

		"return first value if second value is 0" in {
			ApplyMinusB(Cn(4), Cn(0)).simplify must beEqualTo(Cn(4))
		}

		"return minus second value if first value is 0" in {
			ApplyMinusB(Cn(0), Cn(3)).simplify must beEqualTo(ApplyMinusU(Cn(3)))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyMinusB(Cn(3), Ci("X")).simplify must beEqualTo(ApplyMinusB(Cn(3), Ci("X")))
		}
	}

	"derivative" should {
		"obey the subtraction rule: (f - g)' = f' - g' (both terms dx are 0)" in {
			(F - G).dx must beEqualTo(Cn(0))
		}
		
		"obey the subtraction rule: (f - g)' = f' - g' (both terms dx are 0)" in {
			ApplyMinusB(Cn(5), Cn(3)).derivative("X") must beEqualTo(Cn(0))
		}
		
		"obey the subtraction rule: (f - g)' = f' - g' (left side dx is 0)" in {
			ApplyMinusB(Cn(8), Ci("X")).derivative("X") must beEqualTo(ApplyMinusU(Cn(1)))
		}

		"obey the subtraction rule: (f - g)' = f' - g' (right side dx is 0)" in {
			ApplyMinusB(Ci("X"), Cn(3)).derivative("X") must beEqualTo(Cn(1))
		}
		
		"obey the subtraction rule: (f - g)' = f' - g' (neither side dx is 0)" in {
			ApplyMinusB(Ci("X"), Ci("X")).derivative("X") must beEqualTo(Cn(0))
		}
	}
}