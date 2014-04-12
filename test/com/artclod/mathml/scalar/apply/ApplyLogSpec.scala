package mathml.scalar.apply

import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml._
import mathml.scalar._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import math.E

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplyLogSpec extends Specification {

	"eval" should {
		"do natural log" in {
			ApplyLog(6, `36`).eval(Map()).get must beEqualTo(2)
		}
	}

	"variables" should {
		"be empty if elements are constant" in {
			ApplyLog(5, `2`).variables must beEmpty
		}

		"be x if an element constains an x" in {
			ApplyLog(5, x).variables must beEqualTo(Set("x"))
		}
	}

	"c" should {
		"return correct log" in {
			ApplyLog(2.5, Cn(15.625)).c.get must beEqualTo(`3`)
		}

		"fail if not a constant " in {
			ApplyLog(5, x).c must beEmpty
		}
	}

	"s" should {
		"return constant if value is constant" in {
			ApplyLog(5, `25`).s must beEqualTo(`2`)
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyLog(5, x).s must beEqualTo(ApplyLog(5, x))
		}
	}

	"d" should {
		"obey the ln derivative rule: log_b(f)' = f' / ln(b) f" in {
			ApplyLog(E * E, F).dx must beEqualTo(Fdx / (`2` * F))
		}
	}

}