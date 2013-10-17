package mathml

import scala.xml.NamespaceBinding
import scala.xml.MetaData
import scala.util.Try

case class ApplyMinusUnary(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val minus: Minus,
	val value: MathMLElem)
	extends MathMLElem(prefix, "apply", attributes1, scope, minimizeEmpty, (Seq[MathMLElem](minus) ++ value): _*) {

	def this(minus: Minus, value: MathMLElem) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, minus, value)

	def eval(boundVariables: Map[String, Double]) = Try(-1d * value.eval(boundVariables).get)

	def isZero = value.simplify.isZero

	def isOne = false

	def simplify() = {
		if (isZero) Cn(0)
		else if (isOne) Cn(1)
		else this
	}

	def derivative(wrt: String) = ApplyMinusUnary(prefix, attributes1, scope, minimizeEmpty, minus, value.derivative(wrt)).simplify
}

object ApplyMinusUnary {
	def apply(minus: Minus, value: MathMLElem) = new ApplyMinusUnary(minus, value)
	def apply(value: MathMLElem) = new ApplyMinusUnary(Minus(), value)
}