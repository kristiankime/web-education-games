package com.artclod.mathml

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml._
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import com.artclod.mathml.scalar._
import com.artclod.mathml.scalar.apply._
import com.artclod.mathml.scalar.apply.{ ApplyLn => ln }
import com.artclod.mathml.scalar.apply.{ ApplyLog => log }

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLDerivativeSpec extends Specification {

	"Checking symbolic differentiaton and manual derivative " should {

		"confirm ln(x)' = 1 / x" in {
			(ln(x) dx) must beEqualTo(`1` / x)
		}

		// LATER try to simplify these
		//
		//		"1 / ln(x)' = -1 / (x * log(x)^2)" in {
		//			( (`1` / ln(x))ʹ ) must beEqualTo( `-1` / (x * (ln(x) ^ `2`)) )
		//		}
		//		
		//		"x / ln(x)' = (ln(x)-1) / (ln(x)^2)" in {
		//			(x / ln(x)ʹ) must beEqualTo((ln(x) - `1`) / (ln(x) ^ `2`))
		//		}

		//		<apply><times/><apply><times/><cn type="integer">4</cn><apply><power/><ci>x</ci><cn type="integer">3</cn></apply></apply><apply><power/><exponentiale/><ci>x</ci></apply></apply>
		
//		"(4 * x ^ 3 * e ^ x)' = " in {
//			val mathF = MathML(<apply><times/><apply><times/><cn type="integer">4</cn><apply><power/><ci>x</ci><cn type="integer">3</cn></apply></apply><apply><power/><exponentiale/><ci>x</ci></apply></apply>).get
//		    val f =  ((`4` * (x ^ `3`)) * (e ^ x))
//		    
//		    System.err.println(f dx);
//		    
//		    f must beEqualTo(mathF)
//		    
//		    (f dx) must beEqualTo(   Cn(4 * math.E) )
//		}
		
	}

}