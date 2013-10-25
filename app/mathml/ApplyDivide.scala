package mathml

import scala.util.Try
import scala.xml.MetaData
import scala.xml.NamespaceBinding

case class ApplyDivide(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val divide: Divide,
	val numerator: MathMLElem,
	val denominator: MathMLElem)
	extends MathMLElem(prefix, "apply", attributes1, scope, minimizeEmpty, (Seq[MathMLElem](divide) ++ numerator ++ denominator): _*) {

	def this(divide: Divide, numerator: MathMLElem, denominator: MathMLElem) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, divide, numerator, denominator)

	def eval(boundVariables: Map[String, Double]) = Try(numerator.eval(boundVariables).get / denominator.eval(boundVariables).get)

	def isZero = numerator.simplify.isZero && !denominator.simplify.isZero

	def isOne = numerator.simplify == denominator.simplify

	def simplify() = {
		if (isZero) Cn(0)
		else if (isOne) Cn(1)
		else if (denominator.simplify.isOne) numerator
		else this
	}

	def variables: Set[String] = numerator.variables ++ denominator.variables

	def derivative(wrt: String): MathMLElem = {
		val f = numerator
		val fP = f.derivative(wrt).simplify
		val g = denominator
		val gP = g.derivative(wrt).simplify
		// (f/g)' = (f'g - g'f)/g^2
		ApplyDivide(
			ApplyMinusB(
				ApplyTimes(fP, g),
				ApplyTimes(f, gP)),
			ApplyPower(g, Cn(2))).simplify
	}
}

object ApplyDivide {
	def apply(divide: Divide, numerator: MathMLElem, denominator: MathMLElem) = new ApplyDivide(divide, numerator, denominator)
	def apply(numerator: MathMLElem, denominator: MathMLElem) = new ApplyDivide(Divide(), numerator, denominator)
}