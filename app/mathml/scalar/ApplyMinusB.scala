package mathml.scalar

import scala.util._
import scala.xml._
import mathml._


/**
 * ApplyMinus for the Binary case
 */
case class ApplyMinusB(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val minus: Minus,
	val value1: MathMLElem,
	val value2: MathMLElem)
	extends MathMLElem(prefix, "apply", attributes1, scope, minimizeEmpty, (Seq[MathMLElem](minus) ++ value1 ++ value2): _*) {

	def this(minus: Minus, value1: MathMLElem, value2: MathMLElem) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, minus, value1, value2)

	def eval(boundVariables: Map[String, Double]) = Try(value1.eval(boundVariables).get - value2.eval(boundVariables).get)

	def cn: Option[Cn] = (value1.cn, value2.cn) match {
		case (Some(v1), Some(v2)) => Some(v1 - v2)
		case _ => None
	}
	
	def simplify() = {
		if (isZero) Cn(0)
		else if (isOne) Cn(1)
		else if (value2.isZero) value1
		else if (value1.isZero) ApplyMinusU(minus, value2)
		else this
	}

	def variables: Set[String] = value1.variables ++ value2.variables

	def derivative(wrt: String): MathMLElem = (ApplyMinusB(prefix, attributes1, scope, minimizeEmpty, minus, value1.derivative(wrt), value2.derivative(wrt))).simplify
}

object ApplyMinusB {
	def apply(minus: Minus, value1: MathMLElem, value2: MathMLElem) = new ApplyMinusB(minus, value1, value2)
	def apply(value1: MathMLElem, value2: MathMLElem) = new ApplyMinusB(Minus(), value1, value2)
}