package mathml.scalar.apply

import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml._
import mathml.scalar._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

// LATER try out http://rlegendi.github.io/specs2/runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class ApplyPowerSpec extends Specification {

	"eval" should {
		"raise value to power" in {
			ApplyPower(`2`, `3`).eval(Map()).get must beEqualTo(8)
		}
		
		"return 0 if base is 0" in {
			ApplyPower(`0`, e).eval(Map()).get must beEqualTo(0)
		}
		
		"fail if base is nonzero and power results is 0" in {
			ApplyPower(e, `-1000`).eval(Map()) must beFailedTry
		}
	}

	"variables" should {
		"be empty if element is constant" in {
			ApplyPower(`1`, `2`).variables must beEmpty
		}

		"be x if element constains an x" in {
			ApplyPower(x, `2`).variables must beEqualTo(Set("x"))
		}

		"be y if element constains a y" in {
			ApplyPower(y, `2`).variables must beEqualTo(Set("y"))
		}

		"be x & y if element constains x & y" in {
			ApplyPower(x, y).variables must beEqualTo(Set("x", "y"))
		}
	}
	
	"c" should {
		"return 0 if base is 0" in {
			ApplyPower(`0`, x).c.get must beEqualTo(`0`)
		}

		"return 1 if base is 1" in {
			ApplyPower(`1`, x).c.get must beEqualTo(`1`)
		}

		"return base if power is 1" in {
			ApplyPower(`5`, `1`).c.get must beEqualTo(`5`)
		}

		"return 1 if power is 0" in {
			ApplyPower(x, `0`).c.get must beEqualTo(`1`)
		}

		"return None if function is not constant" in {
			ApplyPower(`2`, x).c must beNone
		}
	}

	"s" should {
		"return 0 if base is zero" in {
			ApplyPower(`0`, x).s must beEqualTo(`0`)
		}

		"return 1 if base is 1" in {
			ApplyPower(`1`, x).s must beEqualTo(`1`)
		}

		"return 1 if exponent is 0" in {
			ApplyPower(x, `0`).s must beEqualTo(`1`)
		}

		"return base if exponent is 1" in {
			ApplyPower(x, `1`).s must beEqualTo(x)
		}
	}

	"d" should {

		"obey the elementary power rule: (x^n)' = n*x^(n-1)" in {
			ApplyPower(x, `3`).dx must beEqualTo(`3` * (x ^ `2`))
		}

		"obey the chain power rule: (f^n)' = n*f^(n-1)*f'" in {
			ApplyPower(F, `3`).dx must beEqualTo(ApplyTimes(`3`, F ^ `2`, Fdx))
		}

		// (f^g)' = f^(g-1) * (g f'+f log(f) g')
		"obey the generlized power rule: (f^g)' =  f^(g-1)    * (g * f'  + f * log(f)     * g')" in {
			ApplyPower(F, G).dx must beEqualTo(   ((F^(G-`1`)) * (G * Fdx + F * ApplyLn(F) * Gdx))s )
		}
	}
}