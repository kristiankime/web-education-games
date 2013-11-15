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

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLDerivativeSpec extends Specification {

	"Checking equality between symbolic differentiaton and manual derivative " should {

		"ln(x)' = 1 / x" in {
			(ln(x)ʹ) must beEqualTo(`1` / x)
		}

		"1 / ln(x)' = -1 / (x * log(x)^2)" in {
			( (`1` / ln(x))ʹ ) must beEqualTo( `-1` / (x * (ln(x) ^ `2`)) )
		}
		
//		"ffff x / ln(x)' = (ln(x)-1) / (ln(x)^2)" in {
//			( (x / ln(x))ʹ ) must beEqualTo( (ln(x) - `1`) / (ln(x) ^ `2`) )
//		}
//		
//		"x / ln(x)' = (ln(x)-1) / (ln(x)^2)" in {
//			(x / ln(x)ʹ) must beEqualTo((ln(x) - `1`) / (ln(x) ^ `2`))
//		}

	}

}