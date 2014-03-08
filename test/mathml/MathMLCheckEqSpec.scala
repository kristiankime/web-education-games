package mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml._
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import mathml.scalar._
import mathml.scalar.apply._
import mathml.scalar.apply.{ ApplyLn => ln }
import mathml.scalar.apply.{ ApplyLog => log }
import mathml.Match._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLCheckEqSpec extends Specification {

	"Checking equality between symbolic differentiation and manual derivative " should {

		"confirm x' = x / x " in {
			val f = x dx
			val g = x / x
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm (x^2 - x^2)' = 0 " in {
			val f = ((x ^ `2`) - (x ^ `2`))dx
			val g = `0`
			(f ?= g) must beEqualTo(Yes)
		}

		// <math xmlns="http://www.w3.org/1998/Math/MathML"><apply><ln/><apply><minus/><apply><plus/><apply><divide/><cn type="integer">1</cn><ci>x</ci></apply><apply><power/><ci>x</ci><cn type="integer">2</cn></apply></apply><cn type="integer">9</cn></apply></apply></math> | 
		// ln( 1/x + x^2 - 9)

		// <math xmlns="http://www.w3.org/1998/Math/MathML"><apply><divide/><apply><plus/><apply><power/><apply><minus/><ci>x</ci></apply><cn type="integer">-2</cn></apply><apply><times/><cn type="integer">2</cn><ci>x</ci></apply></apply><apply><minus/><apply><plus/><apply><divide/><cn type="integer">1</cn><ci>x</ci></apply><apply><power/><ci>x</ci><cn type="integer">2</cn></apply></apply><cn type="integer">9</cn></apply></apply></math>                         
		// (-x^(-2) + 2*x)/(1/x + x^2 - 9) 

		//<math xmlns="http://www.w3.org/1998/Math/MathML"></math>

		"confirm (4*x^3*e^x)/(x^2 - 5)' = ? " in {
			val mathF = MathML(<apply><divide/><apply><times/><apply><times/><cn type="integer">4</cn><apply><power/><ci>x</ci><cn type="integer">3</cn></apply></apply><apply><power/><exponentiale/><ci>x</ci></apply></apply><apply><minus/><apply><power/><ci>x</ci><cn type="integer">2</cn></apply><cn type="integer">5</cn></apply></apply>).get
			val f = ((`4` * (x ^ `3`)) * (e ^ x)) / ((x ^ `2`) - `5`)
			f must beEqualTo(mathF) // ensure more readable hand typed is the same as the question
			
			val mathG = MathML(<apply><divide/><apply><minus/><apply><times/><apply><plus/><apply><times/><apply><times/><cn type="integer">12</cn><apply><power/><ci>x</ci><cn type="integer">2</cn></apply></apply><apply><power/><exponentiale/><ci>x</ci></apply></apply><apply><times/><apply><times/><cn type="integer">4</cn><apply><power/><ci>x</ci><cn type="integer">3</cn></apply></apply><apply><power/><exponentiale/><ci>x</ci></apply></apply></apply><apply><minus/><apply><power/><ci>x</ci><cn type="integer">2</cn></apply><cn type="integer">5</cn></apply></apply><apply><times/><apply><times/><apply><times/><apply><times/><cn type="integer">4</cn><apply><power/><ci>x</ci><cn type="integer">3</cn></apply></apply><apply><power/><exponentiale/><ci>x</ci></apply></apply><cn type="integer">2</cn></apply><ci>x</ci></apply></apply><apply><power/><apply><minus/><apply><power/><ci>x</ci><cn type="integer">2</cn></apply><cn type="integer">5</cn></apply><cn type="integer">2</cn></apply></apply>).get
			val g = (((`12` * (x ^ `2`) * (e ^ x) + `4` * (x ^ `3`) * (e ^ x)) * ((x ^ `2`) - `5`) - (`4` * (x ^ `3`) * (e ^ x)) * `2` * x)) / (((x ^ `2`) - `5`) ^ `2`)
			g must beEqualTo(mathG) // ensure more readable hand typed is the same as the answer

			((f dx) ?= g) must beEqualTo(Yes)
		}

	}

}