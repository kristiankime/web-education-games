package mathml.scalar.apply

import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml._
import mathml.scalar._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplyLnSpec extends Specification {

	"eval" should {
		"do natural log" in {
			ApplyLn(e).eval(Map()).get must beEqualTo(1)
		}
	}

	"variables" should {
		"be empty if elements are constant" in {
			ApplyLn(`2`).variables must beEmpty
		}

		"be x if an element constains an x" in {
			ApplyLn(x).variables must beEqualTo(Set("x"))
		}
	}

	"c" should {
		"return correct log" in {
			ApplyLn(e).c.get must beEqualTo(`1`)
		}

		"fail if not a constant " in {
			ApplyLn(x).c must beEmpty
		}
	}

	"s" should {
		"return constant if value is constant" in {
			ApplyLn(e).s must beEqualTo(`1`)
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyLn(x).s must beEqualTo(ApplyLn(x))
		}
	}

	"d" should {
		"obey the ln derivative rule: ln(f)' = f'/f" in {
			ApplyLn(F).dx must beEqualTo(Fdx / F)
		}
	}

}