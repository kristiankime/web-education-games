package mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml._
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml.scalar._
import mathml.scalar.apply._
import mathml.scalar.apply.{ ApplyLn => ln, ApplyLog => log }
import mathml.Match._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLCheckEq10BFinalReviewSpec extends Specification {

	"Checking answers for math10b final review " should {

		"confirm (1/(2x+3)^5)' = (-10)/(2*x+3)^6 " in {
			val f = (`1` / (`2` * x + `3`) ^ `5`) dx
			val g = (`-10`) / ((`2` * x + `3`) ^ `6`)
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm (1/(2x+3)^5)' = (-10)/((2*x+3)^6) " in {
			val f = (`1` / (`2` * x + `3`) ^ `5`) dx
			val g = (`-10`) / ((`2` * x + `3`) ^ `6`)
			(f ?= g) must beEqualTo(Yes)
		}
	}

}