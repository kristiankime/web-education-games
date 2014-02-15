package mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml._
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml.scalar._
import mathml.scalar.apply._
import mathml.scalar.apply.{ ApplyLn => ln }
import mathml.scalar.apply.{ ApplyLog => log }
import mathml.Match._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLCheckEqSpec extends Specification {

	"Checking equality between symbolic differentiation and manual derivative " should {

		"confirm x' = x / x " in {
			val f = x dx
			val g = x / x
			(f ?= g) must beEqualTo(Yes)
		}
		
		"confirm (x^2 - x^2)' = 0 " in {
			val f = ((x^`2`) - (x^`2`))dx
			val g = `0`
			(f ?= g) must beEqualTo(Yes)
		}


	}

}