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

	def isZero = !numerator.simplify.isZero && denominator.simplify.isZero

	def isOne = numerator.simplify == denominator.simplify

	def simplify() = {
		if (isZero) Cn(0)
		else if (isOne) Cn(1)
		else this
	}

	def derivative(wrt: String): Option[MathMLElem] = null
}

object ApplyDivide {
	def apply(divide: Divide, numerator: MathMLElem, denominator: MathMLElem) = new ApplyDivide(divide, numerator, denominator)
	def apply(numerator: MathMLElem, denominator: MathMLElem) = new ApplyDivide(Divide(), numerator, denominator)
}