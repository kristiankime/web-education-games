package mathml

import scala.util._
import scala.xml._
import math._
import mathml.scalar._
import mathml.scalar.apply._
import mathml.scalar.concept.Constant

object Match extends Enumeration {
	type Match = Value
	val Yes, No, Inconclusive = Value
}

import Match._


object MathML {

	def checkEq(variableName: String, eq1: MathMLElem, eq2: MathMLElem) = {
		if (simplifyEquals(eq1, eq2)) {
			true
		} else {
			val varAndVals = for (i <- -5 to 5) yield Map(variableName -> (i.toDouble + 0.01d))

			System.err.println(eq1);
			System.err.println(eq2);

			val eq1s = varAndVals.map(eq1.eval(_))
			val eq2s = varAndVals.map(eq2.eval(_))

			System.err.println(eq1s);
			System.err.println(eq2s);

			val matches = varAndVals.map(varAndVal => closeEnough(eq1.eval(varAndVal), eq2.eval(varAndVal)))
			
			System.err.println(matches)
			
			matches.contains(Yes) && !matches.contains(No)
		}
	}

	def simplifyEquals(eq1: MathMLElem, eq2: MathMLElem) = {
		eq1.s == eq2.s
	}

	private val accuracy = .00001d; // LATER this is a hack to ensure equality even with some inaccuracy due to double computations
	private def closeEnough(v1: Try[Double], v2: Try[Double]) = {
		(v1, v2) match {
			case (Success(x), Success(y)) => doubleCloseEnough(x, y)
			case (Failure(_), Failure(_)) => Inconclusive // LATER ensure errors are the same?
			case (Failure(_), Success(_)) => No
			case (Success(_), Failure(_)) => No
		}
	}

	private def doubleCloseEnough(x: Double, y: Double) = {
		if (x.isNaN && doubleNonNumber(y)) Inconclusive
		else if (doubleNonNumber(x) && y.isNaN) Inconclusive
		else if (x.isNegInfinity && y.isNegInfinity) Inconclusive
		else if (x.isPosInfinity && y.isPosInfinity) Inconclusive
		else if (x.isNegInfinity && y.isPosInfinity) No
		else if (x.isPosInfinity && y.isNegInfinity) No
		else if ((x - accuracy) <= y && (x + accuracy >= y)) Yes
		else No
	}

	private def doubleNonNumber(d: Double) = {
		d.isInfinite() || d.isNaN()
	}

	def apply(xml: Elem): Try[MathMLElem] = {
		xml.label.toLowerCase match {
			case "math" => Try(Math(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty, MathML(xml.childElem(0)).get))
			case "apply" => applyElement(xml)
			case "cn" => constantElement(xml)
			case "ci" => Success(Ci(xml.child(0).text)) // LATER child(0).text could be nonsense
			case "logbase" => Cn(xml.childElem(0)).map(Logbase(_)) // LATER need to handle special Constants, xml.childElem(0) could fail
			case _ => Failure(new IllegalArgumentException(xml + " was not recognized as a MathML element"))
		}
	}

	private def constantElement(xml: Elem): Try[Constant] = {
		Cn(xml.child(0))
	}

	private def applyElement(xml: Elem): Try[MathMLElem] = {
		if (xml.childElem.size < 2) {
			Failure(new IllegalArgumentException("Apply MathML Elements must have at least two children " + xml))
		} else {
			val apply = xml
			val operator = xml.childElem(0)
			val argumentsTry = xml.childElem.drop(1).map(MathML(_))
			val failure = argumentsTry.find(_.isFailure)

			if (failure.nonEmpty) failure.get
			else applyElementCreate(apply, operator, argumentsTry.map(_.get))
		}
	}

	private def applyElementCreate(a: Elem, o: Elem, args: Seq[MathMLElem]): Try[MathMLElem] = {
		(o.label.toLowerCase(), args) match {
			case ("plus", _) => Success(ApplyPlus(args: _*))
			case ("minus", Seq(v)) => Success(ApplyMinusU(v))
			case ("minus", Seq(v1, v2)) => Success(ApplyMinusB(v1, v2))
			case ("minus", _) => Failure(new IllegalArgumentException(a + " minus was called with >2 arguments"))
			case ("times", _) => Success(ApplyTimes(args: _*))
			case ("divide", Seq(num, den)) => Success(ApplyDivide(num, den))
			case ("divide", _) => Failure(new IllegalArgumentException(a + " divide was called with !=2 arguments"))
			case ("power", Seq(base, exp)) => Success(ApplyPower(base, exp))
			case ("power", _) => Failure(new IllegalArgumentException(a + " power was called with !=2 arguments"))
			case ("ln", Seq(value)) => Success(ApplyLn(value))
			case ("ln", Seq(_)) => Failure(new IllegalArgumentException(a + " ln was called with !=1 arg "))
			case ("log", Seq(value)) => Success(ApplyLog10(value))
			case ("log", Seq(b: Logbase, value)) => Success(ApplyLog(b.v, value))
			case ("log", Seq(_)) => Failure(new IllegalArgumentException(a + " log was called with >1 arg or no logbase"))
			case (a, c) => Failure(new IllegalArgumentException(o + " was not recognized as an applyable MathML element (label [" + o.label + "] might not be recognized or wrong number of child elements [" + c.length + "])"))
		}
	}

	implicit class PimpedElem(e: Elem) {
		def childElem = e.child.collect(_ match { case x: Elem => x })
	}

	val h = <hack/> // This is a hack so we can get default XML meta data for default MathML objects

}



