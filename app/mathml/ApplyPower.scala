package mathml

import scala.util.Try
import scala.xml.MetaData
import scala.xml.NamespaceBinding

case class ApplyPower(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val power: Power,
	val value1: MathMLElem,
	val value2: MathMLElem)
	extends MathMLElem(prefix, "apply", attributes1, scope, minimizeEmpty, (Seq[MathMLElem](power) ++ value1 ++ value2): _*) {

	def this(power: Power, value1: MathMLElem, value2: MathMLElem) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, power, value1, value2)

	def eval(boundVariables: Map[String, Double]) = Try(Math.pow(value1.eval(boundVariables).get, value2.eval(boundVariables).get))

	def isZero = value1.simplify.isZero

	def isOne = value1.simplify.isOne || value2.simplify.isZero

	def simplify() = {
		if (isZero) Cn(0)
		else if (isOne) Cn(1)
		else if (value2.isOne) value1
		else this
	}

	def derivative(wrt: String): MathMLElem = null
}

object ApplyPower {
	def apply(power: Power, value1: MathMLElem, value2: MathMLElem) = new ApplyPower(power, value1, value2)
	def apply(value1: MathMLElem, value2: MathMLElem) = new ApplyPower(Power(), value1, value2)
}