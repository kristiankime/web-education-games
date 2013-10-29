package mathml.scalar

import scala.util._
import scala.xml._
import mathml._


case class ApplyPower(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val power: Power,
	val base: MathMLElem,
	val exp: MathMLElem)
	extends MathMLElem(prefix, "apply", attributes1, scope, minimizeEmpty, (Seq[MathMLElem](power) ++ base ++ exp): _*) {

	def this(power: Power, base: MathMLElem, exp: MathMLElem) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, power, base, exp)

	def eval(boundVariables: Map[String, Double]) = Try(math.pow(base.eval(boundVariables).get, exp.eval(boundVariables).get))

	def cn: Option[Cn] = (base.cn, exp.cn) match {
		case (Some(b), Some(e)) => Some(b ^ e)
		case _ => None
	}
	
	def simplify() = {
		if (isZero) Cn(0)
		else if (isOne) Cn(1)
		else if (exp.isOne) base
		else this
	}

	def variables: Set[String] = base.variables ++ exp.variables

	// LATER technically need to use generalized power rule but for now we'll assume base is f(x) and Real Exponents
	def derivative(wrt: String): MathMLElem = {
		if (!variables.contains(wrt)) Cn(0)
		else if (!exp.variables.contains(wrt)) {
			val r = exp.simplify
			val f = base
			val fP = f.d(wrt).simplify
			// (f(x)^r)' = r*f(x)^(r-1)*f'(x)
			(r * f ^(r - Cn(1)) * fP).simplify
		} else {
			throw new IllegalArgumentException("Differentiation of general power case TBD " + this)
		}
	}
}

object ApplyPower {
	def apply(power: Power, value1: MathMLElem, value2: MathMLElem) = new ApplyPower(power, value1, value2)
	def apply(value1: MathMLElem, value2: MathMLElem) = new ApplyPower(Power(), value1, value2)
}