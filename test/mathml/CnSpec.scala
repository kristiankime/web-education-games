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
class CnSpec extends Specification {

	"isZero" should {
		"return true if the number is zero" in {
			Cn(0).isZero must beTrue
		}

		"return false if the number is not zero" in {
			Cn(10).isZero must beFalse
		}
	}

	"isOne" should {
		"return true if the number is one" in {
			Cn(1).isOne must beTrue
		}

		"return false if the number is not one" in {
			Cn(10).isOne must beFalse
		}
	}

	"simplify" should {
		"return value unchanged" in {
			Cn(1).simplify must beEqualTo(Cn(1))
		}
	}

	"derivative" should {
		"return zero" in {
			Cn(10).derivative("X") must beEqualTo(Cn(0))
		}
	}
}