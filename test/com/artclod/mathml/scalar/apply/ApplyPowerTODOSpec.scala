package mathml.scalar.apply

import org.specs2.mutable._
import mathml.scalar._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

// LATER try out http://rlegendi.github.io/specs2/runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplyPowerTODOSpec extends Specification {

  skipAll

	"d" should {
    "handle negative bases" in {
      (ApplyPower(`-2`, x).dx must beEqualTo(`-2` ^ x * ApplyLn(`2`)))
    }
	}

}