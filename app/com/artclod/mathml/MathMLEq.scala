package com.artclod.mathml

import com.artclod.mathml.scalar._

import scala.util._

object Match extends Enumeration {
	type Match = Value
	val Yes, No, Inconclusive = Value
}

import com.artclod.mathml.Match._

object MathMLEq {
	private val ran = new Random(0L) // At least for now use a fixed set of pseudo random values
	private val vals = (Vector.fill(20)((ran.nextDouble * 1000d) - 500d) ++ Vector.fill(20)((ran.nextDouble * 10d) - 5d)).sorted
	private val tooSmall = 1E-154 // LATER figure out how small is too small :( i.e. 1e-312 works for most tests...
	private val tooBig = 1E154 // LATER figure out how big is too big
	private val ε = .00001d
  private val closeEnoughTo0 = 1E-15

	def checkEq(variableName: String, eq1: MathMLElem, eq2: MathMLElem) = checkEval(variableName, eq1, eq2, vals)

	def checkEval(vn: String, eq1: MathMLElem, eq2: MathMLElem, vals: Seq[Double]): Match = {
		val eq1s = vals.map(v => eq1.eval(Map(vn -> v.doubleValue())))
		val eq2s = vals.map(v => eq2.eval(Map(vn -> v.doubleValue())))
		val matches = eq1s.zip(eq2s).map(v => closeEnough(v._1, v._2))

//    System.err.println("eq1")
//    System.err.println(eq1)
//    System.err.println("eq2")
//    System.err.println(eq2)
//    System.err.println("eq2")
//		System.err.println((vals, eq1s.zip(eq2s), matches).zipped.map((a, b, c) => "val=[" + a + "] evals=[" + b + "] match=[" + c + "]\n"))

    matches.reduce(matchCombine)

//		matches.reduce( (a : Match.Value, b: Match.Value) => (a , b) match {
//			case (No, _) => No
//			case (_, No) => No // If we ever see a No they are not a match
//			case (Yes, _) => Yes
//			case (_, Yes) => Yes // If we have Inconclusive and yes conclude yes
//			case (Inconclusive, Inconclusive) => Inconclusive // If we only have Inconclusive...
//		})
	}

  def matchCombine(a : Match.Value, b: Match.Value) : Match = (a , b) match {
    case (No, _) => No
    case (_, No) => No // If we ever see a No they are not a match
    case (Yes, _) => Yes
    case (_, Yes) => Yes // If we have Inconclusive and yes conclude yes
    case (Inconclusive, Inconclusive) => Inconclusive // If we only have Inconclusive...
  }

	private def closeEnough(v1: Try[Double], v2: Try[Double]) =
		(v1, v2) match {
			case (Success(x), Success(y)) => doubleCloseEnough(x, y)
			case (_, _) => Inconclusive
		}

	def doubleCloseEnough(x: Double, y: Double) : Match  = {
		if (x.isNaN || y.isNaN || x.isInfinite || y.isInfinite) Inconclusive
		else if (x == y) Yes
    else if (x == 0d && y.abs <= closeEnoughTo0) Yes
    else if (y == 0d && x.abs <= closeEnoughTo0) Yes
		else if (x.abs < tooSmall && y.abs < tooSmall) Inconclusive
		else if (x.abs > tooBig && y.abs > tooBig) Inconclusive
		else if (doubleNumbersCloseEnough(x, y)) Yes
		else No
	}

	def doubleNumbersCloseEnough(x: Double, y: Double) = (x - y).abs <= ε * (x.abs + y.abs)

	//	def doubleNumbersCloseEnough(x: Double, y: Double) = {
	//		val ret = (x - y).abs <= ε * (x.abs + y.abs)
	//
	//		if (!ret) {
	//			System.err.println("x                   = " + x)
	//			System.err.println("y                   = " + y)
	//			System.err.println("ε * (x.abs + y.abs) = " + ε * (x.abs + y.abs))
	//			System.err.println("(x - y).abs         = " + (x - y).abs)
	//			System.err.println("ratio               = " + (x - y).abs / (x.abs + y.abs))
	//			System.err.println
	//		}
	//
	//		ret
	//	}

  //  def main(args: Array[String]) {
  //
  //    var x = -1d
  //    while( math.pow(math.E, x) == (2.718281828459045 * math.pow(math.E, x - 1d)) ) {
  //      System.err.println(x)
  //      System.err.println(math.pow(math.E, x))
  //      System.err.println((2.718281828459045 * math.pow(math.E, x - 1d)))
  //      x = x - 1d
  //    }
  //
  //    System.err.println("failed at")
  //    System.err.println(x)
  //    System.err.println(math.pow(math.E, x))
  //    System.err.println((2.718281828459045 * math.pow(math.E, x - 1d)))
  //
  //  }
}
