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
class ApplyMinusUSpec extends Specification {

	"isZero" should {
		"return true if value is zero" in {
			ApplyMinusU(Cn(0)).isZero must beTrue
		}

		"return false if value is nonzero" in {
			ApplyMinusU(Cn(5)).isZero must beFalse
		}
	}

	"isOne" should {
		"return false if values is not -1" in {
			ApplyMinusU(Cn(9)).isOne must beFalse
		}
	}

	"simplify" should {
		"remain unchanged if nothing can be simplified" in {
			ApplyMinusU(Ci("X")).simplify must beEqualTo(ApplyMinusU(Ci("X")))
		}
	}

	"derivative" should {
		"return zero if value is not a function of variable" in {
			ApplyMinusU(Cn(6)).derivative("X") must beEqualTo(Cn(0))
		}
		
		"return minus of deriv if value is a function of variable" in {
			ApplyMinusU(Ci("X")).derivative("X") must beEqualTo(ApplyMinusU(Cn(1)))
		}
	}
}