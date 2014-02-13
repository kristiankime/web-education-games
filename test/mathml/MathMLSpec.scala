package mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml._
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml.scalar._
import mathml.scalar.apply._
import mathml.scalar.apply.trig._
import mathml.Match._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLSpec extends Specification {

	"apply" should {

		"fail to parse non MathML" in {
			MathML(<not_math_ml_tag> </not_math_ml_tag>).isFailure must beTrue
		}

		"be able to parse numbers" in {
			val xml = <cn>5</cn>
			val mathML = `5`
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse variables" in {
			val xml = <ci>x</ci>
			val mathML = x
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse plus with one argument" in {
			val xml = <apply> <plus/> <cn>5</cn> </apply>
			val mathML = ApplyPlus(`5`)
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse plus with two arguments" in {
			val xml = <apply> <plus/> <cn>5</cn> <cn>5</cn> </apply>
			val mathML = ApplyPlus(`5`, `5`)
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse plus with more then two arguments" in {
			val xml = <apply> <plus/> <cn>5</cn> <cn>4</cn> <cn>3</cn> </apply>
			val mathML = ApplyPlus(`5`, `4`, `3`)
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse minus with one argument" in {
			val xml = <apply> <minus/> <cn>5</cn> </apply>
			val mathML = ApplyMinusU(`5`)
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse minus with two arguments" in {
			val xml = <apply> <minus/> <cn>5</cn> <cn>5</cn> </apply>
			val mathML = ApplyMinusB(`5`, `5`)
			MathML(xml).get must beEqualTo(mathML)
		}

		"fail to parse minus with more then two arguments" in {
			MathML(<apply> <minus/> <cn>5</cn> <cn>4</cn> <cn>3</cn> </apply>).isFailure must beTrue
		}

		"be able to parse times" in {
			val xml = <apply> <times/> <cn>5</cn> <cn>5</cn> </apply>
			val mathML = ApplyTimes(`5`, `5`)
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse times with more then two arguments" in {
			val xml = <apply> <times/> <cn>5</cn> <cn>4</cn> <cn>3</cn> </apply>
			val mathML = ApplyTimes(`5`, `4`, `3`)
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse divide" in {
			val xml = <apply> <divide/> <cn>5</cn> <cn>5</cn> </apply>
			val mathML = ApplyDivide(`5`, `5`)
			MathML(xml).get must beEqualTo(mathML)
		}

		"fail to parse divide with more then two arguments" in {
			MathML(<apply> <divide/> <cn>5</cn> <cn>4</cn> <cn>3</cn> </apply>).isFailure must beTrue
		}

		"be able to parse power" in {
			val xml = <apply> <power/> <cn>5</cn> <cn>5</cn> </apply>
			val mathML = ApplyPower(`5`, `5`)
			MathML(xml).get must beEqualTo(mathML)
		}

		"fail to parse power with more then two arguments" in {
			MathML(<apply> <power/> <cn>5</cn> <cn>4</cn> <cn>3</cn> </apply>).isFailure must beTrue
		}

		"be able to parse nested applys" in {
			val xml = <apply> <plus/> <apply> <plus/> <cn>4</cn> <cn>4</cn> </apply> <cn>5</cn> <cn>5</cn> </apply>
			val mathML = ApplyPlus(ApplyPlus(`4`, `4`), `5`, `5`)
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse log with base" in {
			val xml = <apply> <log/> <logbase> <cn>4</cn> </logbase> <cn>16</cn> </apply>
			val mathML = ApplyLog(4, `16`)
			MathML(xml).get must beEqualTo(mathML)
		}

		"fail to parse log with a cn instead of a logbase" in {
			MathML(<apply> <log/> <cn>4</cn> <cn>16</cn> </apply>).isFailure must beTrue
		}

		"parse log without base as log 10" in {
			val xml = <apply> <log/> <cn>16</cn> </apply>
			val mathML = ApplyLog10(`16`)
			MathML(xml).get must beEqualTo(mathML)
		}

		"parse e" in {
			val xml = <exponentiale/>
			MathML(xml).get must beEqualTo(ExponentialE)
		}

		"parse e nested" in {
			val xml = <apply> <plus/> <ci>x</ci> <exponentiale/> </apply>
			MathML(xml).get must beEqualTo(x + e)
		}

		"parse pi" in {
			val xml = <pi/>
			MathML(xml).get must beEqualTo(π)
		}

		"be able to parse cos" in {
			MathML(<apply> <cos/> <pi/> </apply>).get must beEqualTo(ApplyCos(π))
		}

		"be able to parse cot" in {
			MathML(<apply> <cot/> <pi/> </apply>).get must beEqualTo(ApplyCot(π))
		}

		"be able to parse csc" in {
			MathML(<apply> <csc/> <pi/> </apply>).get must beEqualTo(ApplyCsc(π))
		}

		"be able to parse sec" in {
			MathML(<apply> <sec/> <pi/> </apply>).get must beEqualTo(ApplySec(π))
		}

		"be able to parse sin" in {
			MathML(<apply> <sin/> <pi/> </apply>).get must beEqualTo(ApplySin(π))
		}

		"be able to parse tan" in {
			MathML(<apply> <tan/> <pi/> </apply>).get must beEqualTo(ApplyTan(π))
		}

	}

	"checkEq" should {

		"be true for two equal cns" in {
			MathML.checkEq("x", `3`, `3`) must beEqualTo(Yes)
		}

		"be true for two x+2 & 2+x" in {
			val v1 = x + `2`
			val v2 = `2` + x

			MathML.checkEq("x", v1, v2) must beEqualTo(Yes)
		}

		"be true for x^2 & x^2" in {
			val v1 = x ^ `2`
			val v2 = x ^ `2`

			MathML.checkEq("x", v1, v2) must beEqualTo(Yes)
		}

		"be true for [x + x + 2] & [2 * (x + 1)]" in {
			val v1 = x + x + `2`
			val v2 = `2` * (x + `1`)

			MathML.checkEq("x", v1, v2) must beEqualTo(Yes)
		}

		"be false for [x^2] & [x-2]" in {
			val v1 = x ^ `2`
			val v2 = x - `2`

			MathML.checkEq("x", v1, v2) must beEqualTo(Yes)
		}

		"be false for [2*x] & [x]" in {
			val v1 = `2` * x
			val v2 = x

			MathML.checkEq("x", v1, v2) must beEqualTo(No)
		}

		"be false for [x^2] & [1]" in {
			val v1 = x ^ `2`
			val v2 = `1`

			MathML.checkEq("x", v1, v2) must beEqualTo(No)
		}

		"be true for two log(x)s" in {
			MathML.checkEq("x", ApplyLn(x), ApplyLn(x)) must beEqualTo(Yes)
		}

		"be false for two logs with different bases" in {
			MathML.checkEq("x", ApplyLog10(x), ApplyLn(x)) must beEqualTo(No)
		}

		"be false for two logs with different bases" in {
			MathML.checkEq("x", ApplyLog10(x), ApplyLn(x)) must beEqualTo(No)
		}
	}

	"doubleNumbersCloseEnough" should {

		"be true for two identical numbers (near 1)" in {
			MathML.doubleNumbersCloseEnough(1.23e+0, 1.23e+0) must beTrue
		}

		"be true for two close numbers (near 1)" in {
			MathML.doubleNumbersCloseEnough(1.2300005e+0, 1.23e+0) must beTrue
		}

		"be false for two different numbers (near 1)" in {
			MathML.doubleNumbersCloseEnough(1.24e+1, 1.23e+1) must beFalse
		}
		
		"be true for two identical very large numbers " in {
			MathML.doubleNumbersCloseEnough(1.23e+100, 1.23e+100) must beTrue
		}

		"be true for two close very large numbers" in {
			MathML.doubleNumbersCloseEnough(1.2300005e+100, 1.23e+100) must beTrue
		}
		
		"be false for two different very large numbers" in {
			MathML.doubleNumbersCloseEnough(1.24e+100, 1.23e+100) must beFalse
		}
		
		"be true for two identical very small numbers" in {
			MathML.doubleNumbersCloseEnough(1.23e-100, 1.23e-100) must beTrue
		}

		"be true for two close very small numbers" in {
			MathML.doubleNumbersCloseEnough(1.2300005e-100, 1.23e-100) must beTrue
		}
		
		"be false for two different very small numbers" in {
			MathML.doubleNumbersCloseEnough(1.24e-100, 1.23e-100) must beFalse
		}
		
		"be false for one large and one small number" in {
			MathML.doubleNumbersCloseEnough(1.24e+100, 1.23e-100) must beFalse
		}
	}
}