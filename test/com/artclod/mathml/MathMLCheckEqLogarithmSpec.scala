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
import com.artclod.mathml.Match._

// LATER try out http://rlegendi.github.io/specs2-runner/ and remove RunWith
@RunWith(classOf[JUnitRunner])
class MathMLCheckEqLogarithmSpec extends Specification {

	"Checking equality between symbolic differentiation and manual derivative " should {
				
		"confirm ln(x)' = 1 / x" in {
			val f = ln(x) dx
			val g = `1` / x
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm log(4, x)' = 1 / (x ln(4))" in {
			val f = log(4, x) dx
			val g = `1` / (x * ln(`4`))
			(f ?= g) must beEqualTo(Yes)
		}
		
		"confirm 1 / ln(x)' = -1 / (x * log(x)^2)" in {
			val f = (`1` / ln(x)) dx
			val g = `-1` / (x * (ln(x) ^ `2`))
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm x / ln(x)' = (ln(x)-1) / (ln(x)^2)" in {
			val f = (x / ln(x)) dx
			val g = (ln(x) - `1`) / (ln(x) ^ `2`)
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm (x/(x+ln(x)))' = (ln(x)-1) / (x+ln(x))^2" in {
			val f = x / (x + ln(x)) dx
			val g = (ln(x) - `1`) / ((x + ln(x)) ^ `2`)
			(f ?= g) must beEqualTo(Yes)
		}

		"confirm (x/(x+log(x)))' = (ln(10)*(ln(x) - 1))/(x*ln(10)+ln(x))^2" in {
			val f = (x / (x + log(x))) dx
			val g = (ln(`10`) * (ln(x) - `1`)) / ((x * ln(`10`) + ln(x)) ^ `2`)
			(f ?= g) must beEqualTo(Yes)
		}

	}

}