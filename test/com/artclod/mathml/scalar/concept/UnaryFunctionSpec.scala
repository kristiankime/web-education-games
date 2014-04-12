package mathml.scalar.concept

import org.specs2.runner.JUnitRunner
import org.specs2.mutable._
import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import mathml._
import mathml.scalar._
import mathml.scalar.apply._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove import mathml.scalar.Fdx
@RunWith(classOf[JUnitRunner])
class UnaryFunctionSpec extends Specification {

	"variables" should {
		"return empty for constant internals" in {
			DummyUnaryFunction(`1`).variables must beEqualTo(Set())
		}

		"return the variable if it has one" in {
			DummyUnaryFunction(x).variables must beEqualTo(Set("x"))
		}
	}

}