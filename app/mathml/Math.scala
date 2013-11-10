package mathml

import scala.util._
import scala.xml._
import mathml.scalar.MathMLElem
import mathml.scalar.Cn
import mathml.scalar.Constant

case class Math(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val value: MathMLElem)
	extends MathMLElem(prefix, "math", attributes1, scope, minimizeEmpty, Seq(value): _*) {

	def this(value: MathMLElem) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, value)

	def eval(boundVariables: Map[String, Double]) = value.eval(boundVariables)

	def cnStep: Option[Constant] = value.cnStep
	
	def simplifyStep() = Math(prefix, attributes, scope, minimizeEmpty, value.simplifyStep)

	def variables: Set[String] = value.variables

	def derivative(wrt: String) = Math(prefix, attributes, scope, minimizeEmpty, value.d(wrt).s)
}

object Math {
	def apply(value: MathMLElem) = new Math(value)
}
