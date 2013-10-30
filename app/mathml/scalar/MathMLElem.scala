package mathml.scalar

import scala.util._
import scala.xml._
import mathml._

abstract class MathMLElem(
	prefix: String,
	label: String,
	attributes1: MetaData,
	scope: NamespaceBinding,
	minimizeEmpty: Boolean,
	child: Node*)
	extends Elem(prefix, label, attributes1, scope, minimizeEmpty, child: _*) {

	def eval(boundVariables: Map[String, Double]): Try[Double]

	def isZero: Boolean = if (cnStep.nonEmpty) cnStep.get == Cn(0) else false

	def isOne: Boolean = if (cnStep.nonEmpty) cnStep.get == Cn(1) else false

	/**
	 * Does "one level" of attempting to turn this element into a constant.
	 * Implementations of this method should not use the "c" or "s" or "simplifyStep" methods.
	 */
	def cnStep: Option[Cn]

	def c = s.cnStep

	/**
	 * Does one round of simplification on this element
	 * Implementations of this method should not use the "c" or "s" methods (but can use cnStep).
	 */
	def simplifyStep(): MathMLElem

	// LATER implement this via repeated calls to simplifyStep
	def s = simplifyStep

	def variables: Set[String]

	def derivative(wrt: String): MathMLElem

	def d(wrt: String) = derivative(wrt)

	def dx = derivative("x")

	def +(m: MathMLElem) = ApplyPlus(this, m)

	def *(m: MathMLElem) = ApplyTimes(this, m)

	def -(m: MathMLElem) = ApplyMinusB(this, m)

	def /(m: MathMLElem) = ApplyDivide(this, m)

	def ^(m: MathMLElem) = ApplyPower(this, m)
}

