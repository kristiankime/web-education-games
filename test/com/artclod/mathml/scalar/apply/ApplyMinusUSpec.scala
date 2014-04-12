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
class ApplyMinusUSpec extends Specification {

	"eval" should {
		"return negative of value" in {
			ApplyMinusU(`6`).eval(Map()).get must beEqualTo(-6)
		}
	}
	
	"variables" should {
		"be empty if element is constant" in {
			ApplyMinusU(`2`).variables must beEmpty
		}

		"be x if element constains an x" in {
			ApplyMinusU(x).variables must beEqualTo(Set("x"))
		}

		"be y if element constains a y" in {
			ApplyMinusU(y).variables must beEqualTo(Set("y"))
		}
	}
	
	"c" should {
		"return 0 if value is 0" in {
			ApplyMinusU(`0`).c.get must beEqualTo(`0`)
		}

		"return 1 if value is -1" in {
			ApplyMinusU(`-1`).c.get must beEqualTo(`1`)
		}

		"return negative of a value" in {
			ApplyMinusU(`3`).c.get must beEqualTo(`-3`)
		}

		"fail if not a constant " in {
			ApplyMinusU(x).c must beNone
		}
	}

	"s" should {
		"return constant if value is constant" in {
			ApplyMinusU(`-4`).s must beEqualTo(`4`)
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyMinusU(x).s must beEqualTo(ApplyMinusU(x))
		}
	}

	"d" should {
		"return negative of values derivative" in {
			ApplyMinusU(F).dx must beEqualTo(ApplyMinusU(Fdx))
		}
	}

}