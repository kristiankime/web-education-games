package com.artclod.mathml

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

    "x^(-2/3) @ 0 is not defined" in {
      MathMLDefined.isDefinedAt(x ^ (`-2` / `3`), "x" -> 0) must beFalse
    }

    "x^(1/3)' @ 0 is not defined" in {
      val f = x ^ (`1` / `3`)
      MathMLDefined.isDefinedAt(f dx, "x" -> 0) must beFalse
    }

    "tan(x/2)' @ pi is not defined" in {
      MathMLDefined.isDefinedAt(  (sec(x / 2) ^ 2) * (`1` / `2`), "x" -> math.Pi) must beFalse
    }
  }

  "isDefinedFor" should {
    "return true for x (for 100%)" in {
      MathMLDefined.isDefinedFor( x , 1.00d) must beTrue
    }

    "return true for 1/x (for 99%)" in {
      MathMLDefined.isDefinedFor( `1` / x , 0.99d) must beTrue
    }

    "return true for ln(x) (for 40%)" in {
      MathMLDefined.isDefinedFor( ln(x) , 0.40d) must beTrue
    }

    "return false for ln(x) (for 60%)" in {
      MathMLDefined.isDefinedFor( ln(x) , 0.60d) must beFalse
    }
  }

}
