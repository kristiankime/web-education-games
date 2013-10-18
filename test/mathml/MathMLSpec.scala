package mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.XML
import scala.xml.Text
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import org.specs2.matcher.Matcher

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLSpec extends Specification {

	"apply" should {

		"fail to parse non MathML" in {
			MathML(<not_math_ml_tag> </not_math_ml_tag>).isFailure must beTrue
		}

		"be able to parse numbers" in {
			val xml = <cn>5</cn>
			val mathML = Cn(5)
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse variables" in {
			val xml = <ci>X</ci>
			val mathML = Ci("X")
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse plus with one argument" in {
			val xml = <apply> <plus/> <cn>5</cn> </apply>
			val mathML = ApplyPlus(Cn(5))
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse plus with two arguments" in {
			val xml = <apply> <plus/> <cn>5</cn> <cn>5</cn> </apply>
			val mathML = ApplyPlus(Cn(5), Cn(5))
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse plus with more then two arguments" in {
			val xml = <apply> <plus/> <cn>5</cn> <cn>4</cn> <cn>3</cn> </apply>
			val mathML = ApplyPlus(Cn(5), Cn(4), Cn(3))
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse minus with one argument" in {
			val xml = <apply> <minus/> <cn>5</cn> </apply>
			val mathML = ApplyMinusU(Cn(5))
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse minus with two arguments" in {
			val xml = <apply> <minus/> <cn>5</cn> <cn>5</cn> </apply>
			val mathML = ApplyMinusB(Cn(5), Cn(5))
			MathML(xml).get must beEqualTo(mathML)
		}

		"fail to parse minus with more then two arguments" in {
			MathML(<apply> <minus/> <cn>5</cn> <cn>4</cn> <cn>3</cn> </apply>).isFailure must beTrue
		}

		"be able to parse times" in {
			val xml = <apply> <times/> <cn>5</cn> <cn>5</cn> </apply>
			val mathML = ApplyTimes(Cn(5), Cn(5))
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse times with more then two arguments" in {
			val xml = <apply> <times/> <cn>5</cn> <cn>4</cn> <cn>3</cn> </apply>
			val mathML = ApplyTimes(Cn(5), Cn(4), Cn(3))
			MathML(xml).get must beEqualTo(mathML)
		}

		"be able to parse divide" in {
			val xml = <apply> <divide/> <cn>5</cn> <cn>5</cn> </apply>
			val mathML = ApplyDivide(Cn(5), Cn(5))
			MathML(xml).get must beEqualTo(mathML)
		}

		"fail to parse divide with more then two arguments" in {
			MathML(<apply> <divide/> <cn>5</cn> <cn>4</cn> <cn>3</cn> </apply>).isFailure must beTrue
		}

		"be able to parse power" in {
			val xml = <apply> <power/> <cn>5</cn> <cn>5</cn> </apply>
			val mathML = ApplyPower(Cn(5), Cn(5))
			MathML(xml).get must beEqualTo(mathML)
		}

		"fail to parse power with more then two arguments" in {
			MathML(<apply> <power/> <cn>5</cn> <cn>4</cn> <cn>3</cn> </apply>).isFailure must beTrue
		}

		"be able to parse nested applys" in {
			val xml = <apply> <plus/> <apply> <plus/> <cn>4</cn> <cn>4</cn> </apply> <cn>5</cn> <cn>5</cn> </apply>
			val mathML = ApplyPlus(ApplyPlus(Cn(4), Cn(4)), Cn(5), Cn(5))
			MathML(xml).get must beEqualTo(mathML)
		}
	}

}