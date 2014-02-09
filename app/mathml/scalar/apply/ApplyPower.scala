package mathml.scalar.apply

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept.Constant

case class ApplyPower(val base: MathMLElem, val exp: MathMLElem)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq[MathMLElem](Power) ++ base ++ exp): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(math.pow(base.eval(boundVariables).get, exp.eval(boundVariables).get))

	def constant: Option[Constant] = (base.c, exp.c) match {
		case (Some(b), _) if (b.isZero) => Some(`0`)
		case (Some(b), _) if (b.isOne) => Some(`1`)
		case (_, Some(e)) if (e.isZero) => Some(`1`)
		case (Some(b), Some(e)) => Some(b ^ e)
		case _ => None
	}

	def simplifyStep() = {
		if (base.isOne) `1`
		else if (exp.isZero) `1`
		else if (exp.isOne) base.s
		else base.s ^ exp.s
	}

	def variables: Set[String] = base.variables ++ exp.variables

	def derivative(x: String): MathMLElem = {
		//		if (!variables.contains(x)) `0`
		//		else if (!exp.variables.contains(x)) {
		//			val r = exp.s
		//			val f = base.s
		//			val fP = f.d(x)
		//			// (f(x)^r)' = r*f(x)^(r-1)*f'(x)
		//			ApplyTimes(r, f ^ (r - `1`).s, fP).s
		//		} else {
		// d/dx (f ^ g) = f^(g-1) * (g * f' + f * log(f) * g')
		val f = base.s
		val fP = f.d(x)
		val g = exp.s
		val gP = g.d(x)

		val first = (f ^ (g - `1`))
		val second = (g * fP + f * ApplyLn(f) * gP)
		val secondSimp = (g * fP + f * ApplyLn(f) * gP) s

		val foo = first * secondSimp
		val fooSimp = foo.s
		fooSimp
		//			(f ^ (g - `1`)) * (g * fP + f * ApplyLn(f) * gP)
		//		}
	}
}