package mathml

import scala.util.Try
import scala.xml.MetaData
import scala.xml.NamespaceBinding

case class ApplyMinusBinary(
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

	def isZero = value1.simplify == value2.simplify

	def isOne = value1.simplify.isOne && value2.simplify.isZero

	def simplify() = {
		if (isZero) Cn(0)
		else if (isOne) Cn(1)
		else if (value2.isZero) value1
		else if (value1.isZero) ApplyMinusUnary(minus, value2)
		else this
	}

	def derivative(wrt: String): MathMLElem = ApplyMinusBinary(prefix, attributes1, scope, minimizeEmpty, minus, value1.derivative(wrt), value2.derivative(wrt)).simplify
}

object ApplyMinusBinary {
	def apply(minus: Minus, value1: MathMLElem, value2: MathMLElem) = new ApplyMinusBinary(minus, value1, value2)
	def apply(value1: MathMLElem, value2: MathMLElem) = new ApplyMinusBinary(Minus(), value1, value2)
}