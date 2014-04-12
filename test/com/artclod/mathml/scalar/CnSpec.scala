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
class CnSpec extends Specification {

	"Cn" should {
		"be the same regardless of whitespace with a string input for int" in {
			Cn(" 34   ").get == Cn(34) must beTrue
		}

		"be the same regardless of whitespace with a string input for real" in {
			Cn(" 34.7   ").get == Cn(34.7) must beTrue
		}

		"be the same regardless of whitespace with a node input for int" in {
			val nodeWith34 = <t>    34  </t>.child(0)
			Cn(nodeWith34).get == Cn(34) must beTrue
		}

		"be the same regardless of whitespace with a node input for int" in {
			val nodeWith34_7 = <t>    34.7  </t>.child(0)
			Cn(nodeWith34_7).get == Cn(34.7) must beTrue
		}
	}

	"isZero" should {
		"return true if the number is zero for int" in {
			Cn(0).isZero must beTrue
		}

		"return true if the number is zero for real" in {
			Cn(0d).isZero must beTrue
		}

		"return false if the number is not zero for int" in {
			Cn(10).isZero must beFalse
		}

		"return false if the number is not zero for real" in {
			Cn(10d).isZero must beFalse
		}
	}

	"isOne" should {
		"return true if the number is one for int" in {
			Cn(1).isOne must beTrue
		}

		"return true if the number is one for real" in {
			Cn(1d).isOne must beTrue
		}
		
		"return false if the number is not one for int" in {
			Cn(10).isOne must beFalse
		}

		"return false if the number is not one for real" in {
			Cn(10d).isOne must beFalse
		}
	}

	"simplify" should {
		"return value unchanged for int" in {
			Cn(1).simplifyStep must beEqualTo(Cn(1))
		}
		
		"return value unchanged for real" in {
			Cn(1d).simplifyStep must beEqualTo(Cn(1d))
		}
	}

	"derivative" should {
		"return zero for int" in {
			Cn(10).derivative("X") must beEqualTo(`0`)
		}

		"return zero for real" in {
			Cn(10d).derivative("X") must beEqualTo(`0`)
		}
	}

	"+" should {
		"add two ints" in {
			Cn(1) + Cn(2) must beEqualTo(Cn(3))
		}

		"add two reals" in {
			Cn(1.1) + Cn(2.2) must beEqualTo(Cn(3.3))
		}

		"add real & int" in {
			Cn(1.1) + Cn(2) must beEqualTo(Cn(3.1))
		}

		"add int & real" in {
			Cn(1) + Cn(2.2) must beEqualTo(Cn(3.2))
		}
	}

	"-" should {
		"subtract two ints" in {
			Cn(1) - Cn(2) must beEqualTo(Cn(-1))
		}

		"subtract two reals" in {
			Cn(1.1) - Cn(2.2) must beEqualTo(Cn(-1.1))
		}

		"subtract real & int" in {
			Cn(1.1) - Cn(2) must beEqualTo(Cn(-.9))
		}

		"subtract int & real" in {
			Cn(1) - Cn(2.2) must beEqualTo(Cn(-1.2))
		}
	}

	"*" should {
		"multiply two ints" in {
			Cn(2) * Cn(3) must beEqualTo(Cn(6))
		}

		"multiply two reals" in {
			Cn(2.5) * Cn(4.5) must beEqualTo(Cn(11.25))
		}

		"multiply real & int" in {
			Cn(1.1) * Cn(2) must beEqualTo(Cn(2.2))
		}

		"multiply int & real" in {
			Cn(2) * Cn(2.4) must beEqualTo(Cn(4.8))
		}
	}

	"/" should {
		"divide two ints" in {
			Cn(4) / Cn(8) must beEqualTo(Cn(.5))
		}

		"divide two reals" in {
			Cn(3.75) / Cn(1.5) must beEqualTo(Cn(2.5))
		}

		"divide real & int" in {
			Cn(1.1) / Cn(2) must beEqualTo(Cn(.55))
		}

		"divide int & real" in {
			Cn(5) / Cn(2.5) must beEqualTo(Cn(2))
		}
	}

	"^" should {
		"work with two ints" in {
			Cn(3) ^ Cn(2) must beEqualTo(Cn(9))
		}

		"work with two reals" in {
			Cn(2.25) ^ Cn(.5) must beEqualTo(Cn(1.5))
		}

		"work with real & int" in {
			Cn(1.5) ^ Cn(2) must beEqualTo(Cn(2.25))
		}

		"work with int & real" in {
			Cn(4) ^ Cn(1.5) must beEqualTo(Cn(8))
		}
	}
}