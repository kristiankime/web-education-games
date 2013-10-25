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
class ApplyPlusSpec extends Specification {

	"isZero" should {
		"return true if all values are 0" in {
			ApplyPlus(Cn(0), Cn(0), Cn(0)).isZero must beTrue
		}

		"return false if values don't sum to zero " in {
			ApplyPlus(Cn(1), Cn(2), Cn(2)).isZero must beFalse
		}
	}

	"isOne" should {
		"return true if one value is 1 and the rest are 0" in {
			ApplyPlus(Cn(1), Cn(0), Cn(0)).isOne must beTrue
		}

		"return false if values do not add to 1" in {
			ApplyPlus(Cn(4), Cn(2)).isOne must beFalse
		}
	}

	"simplify" should {
		"return 0 if all values are 0" in {
			ApplyPlus(Cn(0), Cn(0), Cn(0)).simplify must beEqualTo(Cn(0))
		}

		"return 1 if exactly one value is 1" in {
			ApplyPlus(Cn(0), Cn(1), Cn(0)).simplify must beEqualTo(Cn(1))
		}

		"skip any 0 values" in {
			ApplyPlus(Cn(4), Cn(0), Cn(1), Cn(3), Cn(0)).simplify must beEqualTo(ApplyPlus(Cn(4), Cn(1), Cn(3)))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyPlus(Ci("x"), Cn(3)).simplify must beEqualTo(ApplyPlus(Ci("x"), Cn(3)))
		}
	}

	"derivative" should {
		"obey the sum rule: (f + g)' = f' + g' (both terms dx are 0)" in {
			ApplyPlus(Cn(5), Cn(3)).derivative("x") must beEqualTo(Cn(0))
		}
		
		"obey the sum rule: (f + g)' = f' + g' (both terms dx are 0)" in {
			ApplyPlus(Cn(5), Cn(3)).derivative("x") must beEqualTo(Cn(0))
		}
		
		"obey the sum rule: (f + g)' = f' + g' (left side dx is 0)" in {
			ApplyPlus(Cn(3), Ci("x")).derivative("x") must beEqualTo(Cn(1))
		}

		"obey the sum rule: (f + g)' = f' + g' (right side dx is 0)" in {
			ApplyPlus(Ci("x"), Cn(3)).derivative("x") must beEqualTo(Cn(1))
		}
		
		"obey the sum rule: (f + g)' = f' + g' (neither side dx is 0)" in {
			ApplyPlus(Ci("x"), Ci("x")).derivative("x") must beEqualTo(ApplyPlus(Cn(1), Cn(1)))
		}
		
		"obey the sum rule for >2 arguments" in {
			ApplyPlus(Ci("x"), Ci("x"), Ci("x")).derivative("x") must beEqualTo(ApplyPlus(Cn(1), Cn(1), Cn(1)))
		}
	}
}