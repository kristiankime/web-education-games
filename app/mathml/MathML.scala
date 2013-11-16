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

	def checkEq(variableName: String, eq1: MathMLElem, eq2: MathMLElem) = {
		if (simplifyEquals(eq1, eq2)) {
			true
		} else {
			val eval = checkEval(variableName, eq1, eq2, (-50 to 50).map(_.doubleValue))

			eval match {
				case No => false
				case Inconclusive => false
				case Yes => true
			}
		}
	}

	def simplifyEquals(eq1: MathMLElem, eq2: MathMLElem) = {
		eq1.s == eq2.s
	}

	private def closeEnough(v1: Try[Double], v2: Try[Double]) = {
		(v1, v2) match {
			case (Success(x), Success(y)) => doubleCloseEnough(x, y)
			case (Failure(_), Failure(_)) => Inconclusive
			case (Failure(_), Success(_)) => No
			case (Success(_), Failure(_)) => No
		}
	}

	private val accuracy = .00001d; // LATER this is a hack to ensure equality even with some inaccuracy due to double computations
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
			case "exponentiale" => Success(ExponentialE)
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



