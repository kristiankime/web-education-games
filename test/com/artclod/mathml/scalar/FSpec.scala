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
class FSpec extends Specification {

	"d" should {
		"return zero for wrt ! x" in {
			F d("y") must beEqualTo(`0`)
		}

		"return Fx for x" in {
			F d("x") must beEqualTo(Fdx)
		}
	}

}