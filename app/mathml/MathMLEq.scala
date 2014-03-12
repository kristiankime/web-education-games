package mathml

import scala.util._
import scala.xml._
import math._
import mathml.scalar._
import mathml.scalar.apply._
import mathml.scalar.apply.trig._
import mathml.scalar.concept.Constant

object Match extends Enumeration {
	type Match = Value
	val Yes, No, Inconclusive = Value
}

import Match._

object MathMLEq {
	private val ran = new Random(0L) // At least for now use a fixed set of pseudo random values
	private val vals = (Vector.fill(20)((ran.nextDouble * 2000d) - 1000d) ++ Vector.fill(20)((ran.nextDouble * 10d) - 5d)).sorted
	private val tooSmall = 1e-154 // LATER figure out how small is too small :( i.e. 1e-312 works for most tests... 
	private val ε = .00001d

	def checkEq(variableName: String, eq1: MathMLElem, eq2: MathMLElem) = checkEval(variableName, eq1, eq2, vals)

	def checkEval(vn: String, eq1: MathMLElem, eq2: MathMLElem, vals: Seq[Double]): Match = {
		val eq1s = vals.map(v => eq1.eval(Map(vn -> v.doubleValue())))
		val eq2s = vals.map(v => eq2.eval(Map(vn -> v.doubleValue())))
		val matches = eq1s.zip(eq2s).map(v => closeEnough(v._1, v._2))
		
		matches.reduce((_, _) match {
			case (No, _) => No
			case (_, No) => No // If we ever see a No they are not a match
			case (Yes, _) => Yes
			case (_, Yes) => Yes // If we have Inconclusive and yes conclude yes
			case (Inconclusive, Inconclusive) => Inconclusive // If we only have Inconclusive...
		})
	}

	private def closeEnough(v1: Try[Double], v2: Try[Double]) =
		(v1, v2) match {
			case (Success(x), Success(y)) => doubleCloseEnough(x, y)
			case (Failure(_), Failure(_)) => Inconclusive
			case (Failure(_), Success(_)) => No
			case (Success(_), Failure(_)) => No
		}

	private def doubleCloseEnough(x: Double, y: Double) = {
		if (x.isNaN || y.isNaN || x.isInfinite || y.isInfinite) Inconclusive
		else if (x == y) Yes
		else if (x.abs < tooSmall && y.abs < tooSmall) Inconclusive
		else if (doubleNumbersCloseEnough(x, y)) Yes
		else No
	}

	def doubleNumbersCloseEnough(x: Double, y: Double) = (x - y).abs <= ε * (x.abs + y.abs)
	
}
