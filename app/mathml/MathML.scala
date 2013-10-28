package mathml

import scala.util._
import scala.xml._
import math._
import mathml.scalar._

object MathML {

	def checkEq(v: String, eq1: MathMLElem, eq2: MathMLElem) = {
		val ret = if (simplifyEquals(eq1, eq2)) {
			true
		} else {
			val values = for (i <- -5 to 5) yield Map(v -> i.toDouble)
			values.forall(v => closeEnough(eq1.eval(v), eq2.eval(v)))
		}
//		System.err.println("eq1 " + eq1 + " eq2 " + eq2 + " == " + ret);
		ret
	}

	def simplifyEquals(eq1: MathMLElem, eq2: MathMLElem) = {
		eq1.simplify == eq2.simplify
	}

	private val accuracy = .00001d; // LATER this is a hack to ensure equality even with some inaccuracy due to double computations
	private def closeEnough(v1: Try[Double], v2: Try[Double]) = {
		(v1, v2) match {
			case (Success(x), Success(y)) => (x - accuracy) <= y && (x + accuracy >= y)
			case (Failure(_), Failure(_)) => true // LATER ensure errors are the same?
			case (Failure(_), Success(_)) => false
			case (Success(_), Failure(_)) => false
		}
	}

	def apply(xml: Elem): Try[MathMLElem] = {
		xml.label.toLowerCase match {
			// TODO math must be the most outside wrapper
			case "math" => Try(Math(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty, MathML(xml.childElem(0)).get))
			case "apply" => applyElement(xml)
			case "cn" => constantElement(xml)
			case "ci" => Success(Ci(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty, xml.child(0)))
			case _ => Failure(new IllegalArgumentException(xml + " was not recognized as a MathML element"))
		}
	}
	
	private def constantElement(xml: Elem): Try[Cn] = {
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
			case ("plus", _) => Success(new ApplyPlus(a.prefix, a.attributes, a.scope, a.minimizeEmpty, Plus(o.prefix, o.attributes, o.scope), args: _*))
			case ("minus", Seq(v)) => Success(new ApplyMinusU(a.prefix, a.attributes, a.scope, a.minimizeEmpty, Minus(o.prefix, o.attributes, o.scope), v))
			case ("minus", Seq(v1, v2)) => Success(new ApplyMinusB(a.prefix, a.attributes, a.scope, a.minimizeEmpty, Minus(o.prefix, o.attributes, o.scope), v1, v2))
			case ("minus", _) => Failure(new IllegalArgumentException(a + " minus was called with >2 arguments"))
			case ("times", _) => Success(new ApplyTimes(a.prefix, a.attributes, a.scope, a.minimizeEmpty, Times(o.prefix, o.attributes, o.scope), args: _*))
			case ("divide", Seq(num, den)) => Success(new ApplyDivide(a.prefix, a.attributes, a.scope, a.minimizeEmpty, Divide(o.prefix, o.attributes, o.scope), num, den))
			case ("divide", _) => Failure(new IllegalArgumentException(a + " divide was called with !=2 arguments"))
			case ("power", Seq(v1, v2)) => Success(new ApplyPower(a.prefix, a.attributes, a.scope, a.minimizeEmpty, Power(o.prefix, o.attributes, o.scope), v1, v2))
			case ("power", _) => Failure(new IllegalArgumentException(a + " power was called with !=2 arguments"))
			case (_, _) => Failure(new IllegalArgumentException(o + " was not recognized as an applyable MathML element"))
		}
	}

	implicit class PimpedElem(e: Elem) {
		def childElem = e.child.collect(_ match { case x: Elem => x })
	}

	val h = <hack/> // This is a hack so we can get default XML meta data for default MathML objects

}


