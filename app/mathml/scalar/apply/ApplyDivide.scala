package mathml.scalar.apply

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept.Constant

case class ApplyDivide(val numerator: MathMLElem, val denominator: MathMLElem)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq[MathMLElem](Divide) ++ numerator ++ denominator): _*) {

	def eval(vars: Map[String, Double]) = Try(numerator.eval(vars).get / denominator.eval(vars).get)

	def constant: Option[Constant] = (numerator.c, denominator.c) match {
		case (Some(nu), Some(de)) => Some(nu / de)
		case (Some(nu), _) => if (nu.isZero) Some(nu) else None
		case _ => None
	}

	def simplifyStep() =
		if (denominator.isOne) numerator.s
		else if (numerator.isZero && !denominator.isZero) `0`
		else {
			(numerator.s, denominator.s) match {
				case (ApplyDivide(n, d), o) => n / (d * o)
				case (o, ApplyDivide(n, d)) => (o * d) / n
				case (n, d) => ApplyDivide(n, d)
			}
		}

	def variables: Set[String] = numerator.variables ++ denominator.variables

	// Quotient Rule (http://en.wikipedia.org/wiki/Quotient_rule)
	def derivative(x: String): MathMLElem = {
		val f = numerator.s
		val fP = f.d(x)
		val g = denominator.s
		val gP = g.d(x)

		// (f/g)' = (f'g - g'f)/g^2
		(fP * g - gP * f) / (g ^ `2`)
	}
}