package mathml.scalar

import scala.util._
import scala.xml._
import mathml._

case class ApplyPower(val base: MathMLElem, val exp: MathMLElem)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq[MathMLElem](Power) ++ base ++ exp): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(math.pow(base.eval(boundVariables).get, exp.eval(boundVariables).get))

	def cnStep: Option[Constant] = (base.cnStep, exp.cnStep) match {
		case (Some(b), _) if (b.isZero) => Some(`0`)
		case (Some(b), _) if (b.isOne) => Some(`1`)
		case (_, Some(e)) if (e.isZero) => Some(`1`)
		case (Some(b), Some(e)) => Some(b ^ e)
		case _ => None
	}

	def simplifyStep() = {
		if (cnStep.nonEmpty) cnStep.get
		else if (base.isOne) Cn(1)
		else if (exp.isZero) Cn(1)
		else if (exp.isOne) base
		else this
	}

	def variables: Set[String] = base.variables ++ exp.variables

	// LATER technically need to use generalized power rule but for now we'll assume base is f(x) and Real Exponents
	def derivative(wrt: String): MathMLElem = {
		if (!variables.contains(wrt)) `0`
		else if (!exp.variables.contains(wrt)) {
			val r = exp.s
			val f = base.s
			val fP = f.d(wrt).s
			// (f(x)^r)' = r*f(x)^(r-1)*f'(x)
			ApplyTimes(r, f ^ (r - `1`).s, fP).s
		} else {
			throw new IllegalArgumentException("Differentiation of general power case TBD " + this)
		}
	}
}