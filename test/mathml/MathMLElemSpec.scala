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
		
		"add numbers together correlty with apply+plus " in {
			Apply(Plus(),Cn(5),Cn(5)).eval(Map()).get must beEqualTo(10)
		}
	}
}