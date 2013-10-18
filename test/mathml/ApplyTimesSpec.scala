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
class ApplyTimesSpec extends Specification {

	"isZero" should {
		"return true if any values are 0" in {
			ApplyTimes(Cn(1), Cn(0), Cn(1)).isZero must beTrue
		}

		"return false if all values are non 0" in {
			ApplyTimes(Cn(1), Cn(2), Cn(2)).isZero must beFalse
		}
	}

	"isOne" should {
		"return true all values are 1" in {
			ApplyTimes(Cn(1), Cn(1)).isOne must beTrue
		}

		"return false if values do not multiply to 1" in {
			ApplyTimes(Cn(4), Cn(2)).isOne must beFalse
		}
	}

	"simplify" should {
		"return 0 if isZero is true" in {
			ApplyTimes(Cn(1), Cn(0), Cn(1)).simplify must beEqualTo(Cn(0))
		}

		"return 1 if isOne is true" in {
			ApplyTimes(Cn(1), Cn(1), Cn(1)).simplify must beEqualTo(Cn(1))
		}

		"skip any 1 values" in {
			ApplyTimes(Cn(4), Cn(1), Cn(3)).simplify must beEqualTo(ApplyTimes(Cn(4), Cn(3)))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyTimes(Cn(3), Ci("X")).simplify must beEqualTo(ApplyTimes(Cn(3), Ci("X")))
		}
	}

	"derivative" should {
		"obey the product rule: (f g)' = f'g + g'f (both terms dx are 0)" in {
			ApplyTimes(Cn(5), Cn(3)).derivative("X") must beEqualTo(Cn(0))
		}
		
		"obey the product rule: (f g)' = f'g + g'f (left side dx is 0)" in {
			ApplyTimes(Cn(3), Ci("X")).derivative("X") must beEqualTo(Cn(3))
		}

		"obey the product rule: (f g)' = f'g + g'f (right side dx is 0)" in {
			ApplyTimes(Ci("X"), Cn(3)).derivative("X") must beEqualTo(Cn(3))
		}
		
		"obey the product rule: (f g)' = f'g + g'f (neither side dx is 0)" in {
			ApplyTimes(Ci("X"), Ci("X")).derivative("X") must beEqualTo(ApplyPlus(Ci("X"), Ci("X")))
		}
	}
}