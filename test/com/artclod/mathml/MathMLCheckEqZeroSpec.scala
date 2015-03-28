package com.artclod.mathml

import com.artclod.mathml.Match._
import com.artclod.mathml.scalar._
import com.artclod.mathml.scalar.apply.trig.{ApplyCos => cos, ApplyCot => cot, ApplyCsc => csc, ApplySec => sec, ApplySin => sin, ApplyTan => tan}
import com.artclod.mathml.scalar.apply.{ApplyLn => ln, ApplyLog => log}
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLCheckEqZeroSpec extends Specification {

	"Checking equality when functions evaluate to zero " should {

    "confirm log(2^x, 2) = x" in {
      (log(2, `2`^x) ?= x) must beEqualTo(Yes)
    }

    "confirm log(2^x, 2) - x = 0" in {
      ((log(2, `2`^x) - x) ?= `0`) must beEqualTo(Yes)
    }

    "confirm log(10^x, 10) - x = 0" in {
      ((log(10, `10`^x) - x) ?= `0`) must beEqualTo(Yes)
    }

    "confirm log(2^x, 2) - x != 0.0000000001 (ensure non zero doesn't equal zero)" in {
      ((log(2, `2`^x) - x) ?= Cn(0.0000000001)) must beEqualTo(No)
    }
		
	}

}