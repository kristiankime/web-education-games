package mathml.scalar.apply

import org.specs2.runner.JUnitRunner
import org.specs2.mutable._
import org.junit.runner.RunWith
import play.api.test._
import play.api.test.Helpers._
import mathml._
import mathml.scalar._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove import mathml.scalar.Fdx
@RunWith(classOf[JUnitRunner])
class ApplyTimesSpec extends Specification {

	"eval" should {
		"multiply two numbers" in {
			ApplyTimes(`3`, `-2`).eval(Map()).get must beEqualTo(-6)
		}

		"multiply many numbers" in {
			ApplyTimes(`3`, `2`, `4`).eval(Map()).get must beEqualTo(24)
		}
	}

	"variables" should {
		"be empty if element is constant" in {
			ApplyTimes(`1`, `2`).variables must beEmpty
		}

		"be x if element constains an x" in {
			ApplyTimes(x, `2`).variables must beEqualTo(Set("x"))
		}

		"be y if element constains a y" in {
			ApplyTimes(y, `2`).variables must beEqualTo(Set("y"))
		}

		"be x & y if element constains x & y" in {
			ApplyTimes(x, y).variables must beEqualTo(Set("x", "y"))
		}
	}
	
	"c" should {
		"return 0 if any value is zero" in {
			ApplyTimes(`1`, `0`, x).c.get must beEqualTo(`0`)
		}

		"return multiplication of values if possible" in {
			ApplyTimes(`4`, `2`, `1`).c.get must beEqualTo(`8`)
		}

		"return none if values do not multiply to a constant" in {
			ApplyTimes(`4`, x).c.isEmpty must beTrue
		}
	}

	"s" should {
		"return 0 if isZero is true" in {
			ApplyTimes(`1`, `0`, `1`).s must beEqualTo(`0`)
		}

		"return 0 if any value is zero" in {
			ApplyTimes(`1`, `0`, x).s must beEqualTo(`0`)
		}

		"return 1 if isOne is true" in {
			ApplyTimes(`1`, `1`, `1`).s must beEqualTo(`1`)
		}

		"multiple any constanst together" in {
			ApplyTimes(`4`, `1`, `3`).s must beEqualTo(Cn(12))
		}

		"remove 1s in a sequence" in {
			ApplyTimes(`1`, `3`, x).s must beEqualTo(`3` * x)
		}

		"remove 1s" in {
			ApplyTimes(`1`, x).s must beEqualTo(x)
		}
		
		"multiply constants and leave variables, with nested elements  (constands go to end)" in {
			ApplyTimes(x, `4`, y, (`2` * `3`)).s must beEqualTo(ApplyTimes(`24`, x, y))
		}

		"remain unchanged if nothing can be simplified" in {
			ApplyTimes(`3`, x).s must beEqualTo(ApplyTimes(`3`, x))
		}
	}

	"d" should {
		"obey the product rule: (f g)' = f'g + fg'" in {
			ApplyTimes(F, G).dx must beEqualTo(Fdx * G + F * Gdx)
		}

		//		LATER
		//		"obey the multiple product rule: (f g h)' = " in {
		//			ApplyTimes(F, G, H).dx must beEqualTo(Fdx * G + F * Gdx)
		//		}
	}

}