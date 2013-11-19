package mathml.scalar.apply.trig


import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml._
import mathml.scalar._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplyCosSpec extends Specification {

	"eval" should {
		"do sin" in {
			ApplyCos(π).eval(Map()).get must beEqualTo(-1)
		}
	}

	"variables" should {
		"be empty if elements are constant" in {
			ApplyCos(`2`).variables must beEmpty
		}

		"be x if an element constains an x" in {
			ApplyCos(x).variables must beEqualTo(Set("x"))
		}
	}

	"c" should {
		"return correct sin" in {
			ApplyCos(π).c.get must beEqualTo(`-1`)
		}

		"fail if not a constant " in {
			ApplyCos(x).c must beEmpty
		}
	}

	"s" should {
		"return constant if value is constant" in {
			ApplyCos(π).s must beEqualTo(`-1`)
		}

		"simplify what can be simpified" in {
			ApplyCos(NeedsSimp).s must beEqualTo(ApplyCos(Simplified))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyCos(x).s must beEqualTo(ApplyCos(x))
		}
	}

	"d" should {
		"obey the sin derivative rule: ln(f)' = f'/f" in {
			ApplyCos(F).dx must beEqualTo(-ApplySin(F) * Fdx)
		}
	}

}
