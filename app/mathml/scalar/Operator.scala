package mathml.scalar

import scala.util._
import scala.xml._
import mathml._

abstract class Operator(
	override val prefix: String,
	override val label: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding)
	extends MathMLElem(prefix, label, attributes1, scope, true, Seq(): _*) {

	def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException("Applyables should not get evaled, use eval on the surrounding apply element."))

	def cnStep: Option[Constant] = None

	def variables: Set[String] = Set()

	def derivative(wrt: String): MathMLElem = throw new UnsupportedOperationException("Applyables should not get derived, use derive on the surrounding apply element.")
}

case class Plus(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding)
	extends Operator(prefix, "plus", attributes1, scope) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope)

	def simplifyStep() = this
}

object Plus {
	def apply() = new Plus()
}

case class Minus(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding)
	extends Operator(prefix, "minus", attributes1, scope) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope)

	def simplifyStep() = this
}

object Minus {
	def apply() = new Minus()
}

case class Times(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding)
	extends Operator(prefix, "times", attributes1, scope) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope)

	def simplifyStep() = this
}

object Times {
	def apply() = new Times()
}

case class Divide(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding)
	extends Operator(prefix, "divide", attributes1, scope) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope)

	def simplifyStep() = this
}

object Divide {
	def apply() = new Divide()
}

case class Power(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding)
	extends Operator(prefix, "power", attributes1, scope) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope)

	def simplifyStep() = this
}

object Power {
	def apply() = new Power()
}

case class Log(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding)
	extends Operator(prefix, "log", attributes1, scope) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope)

	def simplifyStep() = this
}

object Log {
	def apply() = new Log()
}

case class Ln(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding)
	extends Operator(prefix, "ln", attributes1, scope) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope)

	def simplifyStep() = this
}

object Ln {
	def apply() = new Log()
}