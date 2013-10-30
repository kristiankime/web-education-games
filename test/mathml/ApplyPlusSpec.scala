package mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.XML
import scala.xml.Text
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import org.specs2.matcher.Matcher
import mathml.scalar.ApplyPlus
import mathml.scalar.Cn
import mathml.scalar.Ci

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

	"cnStep" should {
		"return sum of elements if all elements are constants" in {
			ApplyPlus(Cn(2), Cn(3), Cn(-1)).cnStep.get must beEqualTo(Cn(4))
		}

		"return None if any elements are not constant" in {
			ApplyPlus(Cn(2), Ci("x"), Cn(1)).cnStep must beNone
		}
	}

	"simplify" should {
		"return 0 if all values are 0" in {
			ApplyPlus(Cn(0), Cn(0), Cn(0)).simplifyStep must beEqualTo(Cn(0))
		}

		"return 1 if exactly one value is 1" in {
			ApplyPlus(Cn(0), Cn(1), Cn(0)).simplifyStep must beEqualTo(Cn(1))
		}

		"sum values if they are all constant" in {
			ApplyPlus(Cn(4), Cn(0), Cn(1), Cn(3), Cn(0)).simplifyStep must beEqualTo(Cn(8))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyPlus(Ci("x"), Cn(3)).simplifyStep must beEqualTo(ApplyPlus(Ci("x"), Cn(3)))
		}
	}

	"derivative" should {
		"obey the sum rule: (f + g)' = f' + g'" in {
			ApplyPlus(F, G).dx must beEqualTo(ApplyPlus(Fdx, Gdx))
		}
		
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
			ApplyPlus(Ci("x"), Ci("x")).derivative("x") must beEqualTo(Cn(2))
		}

		"obey the sum rule for >2 arguments" in {
			ApplyPlus(Ci("x"), Ci("x"), Ci("x")).derivative("x") must beEqualTo(Cn(3))
		}
	}
}