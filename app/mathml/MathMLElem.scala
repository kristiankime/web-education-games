package mathml

import scala.util.Failure
import scala.util.Try
import scala.xml.Elem
import scala.xml.MetaData
import scala.xml.NamespaceBinding
import scala.xml.Node
import scala.xml.Text

abstract class MathMLElem(
	prefix: String,
	label: String,
	attributes1: MetaData,
	scope: NamespaceBinding,
	minimizeEmpty: Boolean,
	child: Node*)
	extends Elem(prefix, label, attributes1, scope, minimizeEmpty, child: _*) {

	def eval(boundVariables: Map[String, Double]): Try[Double]

	def isZero: Boolean

	def isOne: Boolean

	def simplify(): MathMLElem
	
	def variables: Set[String]

	def derivative(wrt: String): MathMLElem

	def d(wrt: String) = derivative(wrt)

	def dx = derivative("x")

	def +(m : MathMLElem) = ApplyPlus(this, m)

	def *(m : MathMLElem) = ApplyTimes(this, m)

	def -(m : MathMLElem) = ApplyMinusB(this, m)

	def /(m : MathMLElem) = ApplyDivide(this, m)

	def ^(m : MathMLElem) = ApplyPower(this, m)
}

abstract class Applyable(
	override val prefix: String,
	override val label: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding)
	extends MathMLElem(prefix, label, attributes1, scope, true, Seq(): _*) {

	def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException("Applyables should not get evaled, use eval on the surrounding apply element."))

	def isZero = false

	def isOne = false

	def variables: Set[String] = Set()
	
	def derivative(wrt: String): MathMLElem = throw new UnsupportedOperationException("Applyables should not get derived, use derive on the surrounding apply element.")
}

case class Plus(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding)
	extends Applyable(prefix, "plus", attributes1, scope) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope)

	def simplify() = this
}

object Plus {
	def apply() = new Plus()
}

case class Minus(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding)
	extends Applyable(prefix, "minus", attributes1, scope) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope)

	def simplify() = this
}

object Minus {
	def apply() = new Minus()
}

case class Times(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding)
	extends Applyable(prefix, "times", attributes1, scope) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope)

	def simplify() = this
}

object Times {
	def apply() = new Times()
}

case class Divide(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding)
	extends Applyable(prefix, "divide", attributes1, scope) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope)

	def simplify() = this
}

object Divide {
	def apply() = new Divide()
}

case class Power(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding)
	extends Applyable(prefix, "power", attributes1, scope) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope)

	def simplify() = this
}

object Power {
	def apply() = new Power()
}

