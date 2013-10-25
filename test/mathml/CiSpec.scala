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
class CiSpec extends Specification {

	"Ci" should {
		"be the same regardless of whitespace with a string input" in {
			Ci(" X   ") == Ci("X") must beTrue
		}

		"be the same regardless of whitespace with a node input" in {
			val nodeWithX = <t>    X  </t>.child(0)
			Ci(nodeWithX) == Ci("X") must beTrue
		}
	}

	"isZero" should {
		"return false" in {
			Ci("X").isZero must beFalse
		}
	}

	"isOne" should {
		"return false" in {
			Ci("X").isOne must beFalse
		}
	}

	"simplify" should {
		"return value unchanged" in {
			Ci("X").simplify must beEqualTo(Ci("X"))
		}
	}

	"derivative" should {
		"return one if wrt same variable" in {
			Ci("X").derivative("X") must beEqualTo(Cn(1))
		}

		"return zero if wrt different variable" in {
			Ci("X").derivative("Y") must beEqualTo(Cn(0))
		}
		
		"return zero if variable is different case" in {
			Ci("X").derivative("x") must beEqualTo(Cn(0))
		}
	}
}