package com.artclod.mathml.scalar

import scala.util._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import scala.math.BigDecimal.double2bigDecimal
import com.artclod.mathml._
import com.artclod.mathml.scalar._
import com.artclod.mathml.scalar.apply._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLElemSpec extends Specification {

	"+" should {
		"use ApplyPlus" in {
			(F + G) must beEqualTo(ApplyPlus(F, G))
		}

		"sum constants" in {
			(`3` + `4` + `5`) must beEqualTo(`12`)
		}

		"nest when used repeatedly" in {
			(F + G + H) must beEqualTo(ApplyPlus(ApplyPlus(F, G), H))
		}
	}

	"-" should {
		"use ApplyMinusB" in {
			(F - G) must beEqualTo(ApplyMinusB(F, G))
		}

		"subtract constants ApplyMinusB" in {
			(`5` - `3`) must beEqualTo(`2`)
		}

		"nest when used repeatedly" in {
			(F - G - H) must beEqualTo(ApplyMinusB(ApplyMinusB(F, G), H))
		}
	}

	"*" should {
		"use ApplyTimes" in {
			(F * G) must beEqualTo(ApplyTimes(F, G))
		}

		"multiply constants" in {
			(`5` * `2`) must beEqualTo(`10`)
		}

		"nest when used repeatedly" in {
			(F * G * H) must beEqualTo(ApplyTimes(ApplyTimes(F, G), H))
		}
	}

	"/" should {
		"use ApplyDivide" in {
			(F / G) must beEqualTo(ApplyDivide(F, G))
		}

		"divide constants ApplyDivide" in {
			(`5` / `2`) must beEqualTo(Cn(5d / 2d))
		}

		"nest when used repeatedly" in {
			(F / G / H) must beEqualTo(ApplyDivide(ApplyDivide(F, G), H))
		}
	}

	"^" should {
		"use ApplyPower" in {
			(F ^ G) must beEqualTo(ApplyPower(F, G))
		}

		"exponentiate constants" in {
			(`5` ^ `3`) must beEqualTo(Cn(125))
		}

		"nest when used repeatedly" in {
			(F ^ G ^ H) must beEqualTo(ApplyPower(ApplyPower(F, G), H))
		}
	}

	"eval" should {
		"turn Cn into a number if possible" in {
			`5`.eval().get must beEqualTo(5)
		}

		"fail if a Cn can't be parsed into a number" in {
			Cn("not a number").isFailure must beTrue
		}

		"turn Ci into the number specified by the bound parameters" in {
			x.eval(Map("x" -> 3d)).get must beEqualTo(3)
		}

		"fail if there is no entry for a Ci variable name in the bound parameters" in {
			x.eval(Map("no entry for X" -> 3d)).isFailure must beTrue
		}

		"fail if there is only an applyable" in {
			Plus.eval().isFailure must beTrue
		}

		"add 2 numbers correctly for apply+plus " in {
			ApplyPlus(`5`, `5`).eval().get must beEqualTo(10)
		}

		"add > 2 numbers correctly for apply+plus " in {
			ApplyPlus(`5`, `5`, `5`, `5`).eval().get must beEqualTo(20)
		}

		"subtract 1 number correctly for apply+minus " in {
			ApplyMinusU(`6`).eval().get must beEqualTo(-6)
		}

		"subtract 2 numbers correctly for apply+minus " in {
			ApplyMinusB(`6`, `5`).eval().get must beEqualTo(1)
		}

		"multiply 2 numbers correctly for apply+times " in {
			ApplyTimes(`3`, `-2`).eval().get must beEqualTo(-6)
		}

		"multiply > 2 numbers correctly for apply+times " in {
			ApplyTimes(`-12`, `.5`, `-2`, `2`).eval().get must beEqualTo(24)
		}

		"divide 2 numbers correctly for apply+divide " in {
			ApplyDivide(`8`, `4`).eval().get must beEqualTo(2)
		}

		"raise a number to another numbers correctly for apply+power" in {
			ApplyPower(`3`, `2`).eval().get must beEqualTo(9)
		}

		"nested applys work" in {
			ApplyPlus(`1`, ApplyPlus(`2`, `3`)).eval().get must beEqualTo(6)
		}
	}

	"d" should {
		"derivative of a constant is 0 (aka None)" in {
			`3`.d("x") must beEqualTo(`0`)
		}

		"derivative of the wrt variable is 1" in {
			x.d("x") must beEqualTo(`1`)
		}

		"derivative of non wrt variable is 0 (aka None)" in {
			Ci("Not X").d("x") must beEqualTo(`0`)
		}

		"sum of the derivatives is the derivative of the sums" in {
			ApplyPlus(x, x).d("x") must beEqualTo(`2`)
		}

		"sum of the derivatives is the derivative of the sums (simplifies left None)" in {
			ApplyPlus(`1`, x).d("x") must beEqualTo(`1`)
		}

		"sum of the derivatives is the derivative of the sums (simplifies right None)" in {
			ApplyPlus(x, `1`).d("x") must beEqualTo(`1`)
		}

		"sum of the derivatives is the derivative of the sums (simplifies both None)" in {
			ApplyPlus(`1`, `1`).d("x") must beEqualTo(`0`)
		}

		"subtraction of the derivatives is the derivative of the subtractions" in {
			ApplyMinusB(x, x).d("x") must beEqualTo(`0`)
		}

		"subtraction of the derivatives is the derivative of the subtractions (simplifies left None)" in {
			ApplyMinusB(`1`, x).d("x") must beEqualTo(`-1`)
		}

		"subtraction of the derivatives is the derivative of the subtractions (simplifies right None)" in {
			ApplyMinusB(x, `1`).d("x") must beEqualTo(`1`)
		}

		"subtraction of the derivatives is the derivative of the subtractions (simplifies both None)" in {
			ApplyMinusB(`1`, `1`).d("x") must beEqualTo(`0`)
		}

		"product rule" in {
			ApplyTimes(x, x).d("x") must beEqualTo(ApplyPlus(x, x))
		}
	}

	"s" should {
		"simplify this" in {
			val a = MathML(<apply>
				<plus/>
				<apply>
					<times/>
					<Ci>G</Ci>
					<Ci>Fdx</Ci>
				</apply>
				<apply>
					<times/>
					<apply>
						<times/>
						<Ci>F</Ci>
						<apply>
							<ln/>
							<Ci>F</Ci>
						</apply>
					</apply>
					<Ci>Gdx</Ci>
				</apply>
			</apply>).get

			val b = MathML(<apply>
				<plus/>
				<apply>
					<times/>
					<Ci>G</Ci>
					<Ci>Fdx</Ci>
				</apply>
				<apply>
					<times/>
					<Ci>F</Ci>
					<apply>
						<ln/>
						<Ci>F</Ci>
					</apply>
					<Ci>Gdx</Ci>
				</apply>
			</apply>).get
			
			a.s must beEqualTo(b)
		}
	}
	

}