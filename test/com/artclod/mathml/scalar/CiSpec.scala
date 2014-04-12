package com.artclod.mathml.scalar

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import com.artclod.mathml._
import com.artclod.mathml.scalar._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class CiSpec extends Specification {

	"Indentifer.name" should {
		"be the same regardless of whitespace" in {
			Ci(" x   ").identifier.name == "x" must beTrue
		}
	}
	
	"Ci" should {
		"be the same regardless of whitespace with a string input" in {
			Ci(" x   ") == Ci("x") must beTrue
		}
	}

	"isZero" should {
		"return false" in {
			Ci("x").isZero must beFalse
		}
	}

	"isOne" should {
		"return false" in {
			Ci("x").isOne must beFalse
		}
	}

	"simplify" should {
		"return value unchanged" in {
			Ci("x").simplifyStep must beEqualTo(Ci("x"))
		}
	}

	"derivative" should {
		"return one if wrt same variable" in {
			Ci("x").derivative("x") must beEqualTo(Cn(1))
		}

		"return zero if wrt different variable" in {
			Ci("x").derivative("Y") must beEqualTo(Cn(0))
		}
		
		"return zero if variable is different case" in {
			Ci("x").derivative("X") must beEqualTo(Cn(0))
		}
	}
}