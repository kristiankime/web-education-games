package mathml

import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.xml.Elem

object MathML {

	def simplifyEquals(eq1: MathMLElem, eq2: MathMLElem) = {
		val ret = eq1.simplify == eq2.simplify
		System.err.println("simplifyEquals " + eq1.simplify + " " + eq2.simplify + " " + ret);
		ret
	}

	def equals(v: String, eq1: MathMLElem, eq2: MathMLElem) = {
		val values = for (i <- -5 to 5) yield Map(v -> i.toDouble)
		values.foldLeft(true)((a, b) => a && closeEnough(eq1.eval(b).get, eq2.eval(b).get))
	}

	private def closeEnough(v1: Double, v2: Double) = {
		v1 == v2
	}

	def apply(xml: Elem): Try[MathMLElem] = {
		xml.label.toLowerCase match {
			// TODO math must be the most outside wrapper
			case "math" => Try(Math(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty, MathML(xml.childElem(0)).get))
			case "apply" => applyElementPrep(xml)
			case "cn" => Success(Cn(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty, xml.child(0)))
			case "ci" => Success(Ci(xml.prefix, xml.attributes, xml.scope, xml.minimizeEmpty, xml.child(0)))
			case _ => Failure(new IllegalArgumentException(xml + " was not recognized as a MathML element"))
		}
	}

	private def applyElementPrep(xml: scala.xml.Elem): scala.util.Try[mathml.MathMLElem] = {
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

	private def applyElementCreate(a: scala.xml.Elem, o: scala.xml.Elem, args: Seq[mathml.MathMLElem]): scala.util.Try[mathml.MathMLElem] = {
		(o.label.toLowerCase(), args) match {
			case ("plus", _) => Success(new ApplyPlus(a.prefix, a.attributes, a.scope, a.minimizeEmpty, Plus(o.prefix, o.attributes, o.scope, o.minimizeEmpty), args: _*))
			case ("minus", Seq(v)) => Success(new ApplyMinusU(a.prefix, a.attributes, a.scope, a.minimizeEmpty, Minus(o.prefix, o.attributes, o.scope, o.minimizeEmpty), v))
			case ("minus", Seq(v1, v2)) => Success(new ApplyMinusB(a.prefix, a.attributes, a.scope, a.minimizeEmpty, Minus(o.prefix, o.attributes, o.scope, o.minimizeEmpty), v1, v2))
			case ("minus", _) => Failure(new IllegalArgumentException(a + " minus was called with >2 arguments"))
			case ("times", _) => Success(new ApplyTimes(a.prefix, a.attributes, a.scope, a.minimizeEmpty, Times(o.prefix, o.attributes, o.scope, o.minimizeEmpty), args: _*))
			case ("divide", Seq(num, den)) => Success(new ApplyDivide(a.prefix, a.attributes, a.scope, a.minimizeEmpty, Divide(o.prefix, o.attributes, o.scope, o.minimizeEmpty), num, den))
			case ("divide", _) => Failure(new IllegalArgumentException(a + " divide was called with !=2 arguments"))
			case ("power", Seq(v1, v2)) => Success(new ApplyPower(a.prefix, a.attributes, a.scope, a.minimizeEmpty, Power(o.prefix, o.attributes, o.scope, o.minimizeEmpty), v1, v2))
			case ("power", _) => Failure(new IllegalArgumentException(a + " power was called with !=2 arguments"))
			case (_, _) => Failure(new IllegalArgumentException(o + " was not recognized as an applyable MathML element"))
		}
	}

	implicit class PimpedElem(e: Elem) {
		def childElem = e.child.collect(_ match { case x: Elem => x })
	}

	val h = <hack/> // This is a hack so we can get default XML meta data for default MathML objects

}


