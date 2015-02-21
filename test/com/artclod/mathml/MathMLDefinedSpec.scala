package com.artclod.mathml

import com.artclod.mathml.Match._
import com.artclod.mathml.scalar._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MathMLDefinedSpec extends Specification {


  "isDefinedAt" should {

    "return true if well defined" in {
      MathMLDefined.isDefinedAt(`1` / x, "x" -> 1) must beTrue
    }

    "return false if pos infinity" in {
      MathMLDefined.isDefinedAt(`1` / x, "x" -> 0) must beFalse
    }

    "return false if neg infinity" in {
      MathMLDefined.isDefinedAt(`-1` / x, "x" -> 0) must beFalse
    }

    "return false if not defined" in {
      MathMLDefined.isDefinedAt(`0` / x, "x" -> 0) must beFalse
    }

    "return false on Failure" in {
      MathMLDefined.isDefinedAt(e ^ x, "x" -> Double.MinValue) must beFalse
    }
  }

}