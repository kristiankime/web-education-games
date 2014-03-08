package mathml.scalar

import scala.util._
import scala.xml._
import scala.annotation.tailrec
import mathml._
import mathml.scalar.apply._
import mathml.scalar.concept._

abstract class MathMLElem(
	prefix: String,
	label: String,
	attributes1: MetaData,
	scope: NamespaceBinding,
	minimizeEmpty: Boolean,
	child: Node*)
	extends Elem(prefix, label, attributes1, scope, minimizeEmpty, child: _*) {

	def eval(boundVariables: Map[String, Double]): Try[Double]

	def isZero: Boolean = c.map(_.isZero).getOrElse(false)

	def isOne: Boolean = c.map(_.isOne).getOrElse(false)

	/**
	 * Does one round of simplification on this element.
	 * Implementations of this method should not use the "s".
	 */
	protected def simplifyStep: MathMLElem

	private def simplifyStepWithCNCheck: MathMLElem = c.getOrElse(simplifyStep)

	private var s_ : MathMLElem = null
	def s = {
		if (s_ == null) {
			s_ = simplifyRecurse(this)
		}
		s_
	}

	@tailrec private def simplifyRecurse(elem: MathMLElem): MathMLElem = {
		val simp = elem.simplifyStepWithCNCheck
		if (simp == elem) { elem }
		else { simplifyRecurse(simp) }
	}

	protected def constant: Option[Constant]

	private var c_ : Option[Constant] = null
	def c = {
		if (c_ == null) {
			c_ = constant
		}
		c_
	}

	def variables: Set[String]

	protected def derivative(wrt: String): MathMLElem

	def d(wrt: String) =
		if (!variables.contains(wrt)) `0`
		else derivative(wrt).s

	def dx = d("x")

	def +(m: MathMLElem) = ApplyPlus(this, m)

	def *(m: MathMLElem) = ApplyTimes(this, m)

	def -(m: MathMLElem) = ApplyMinusB(this, m)

	def unary_-() = ApplyMinusU(this)

	def /(m: MathMLElem) = ApplyDivide(this, m)

	def ^(m: MathMLElem) = ApplyPower(this, m)

	def ?=(e: MathMLElem) = MathMLEq.checkEq("x", this, e)

}
