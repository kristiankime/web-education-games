package mathml.scalar

import scala.util._
import scala.xml._
import mathml._

abstract class Operator(override val label: String)
	extends MathMLElem(MathML.h.prefix, label, MathML.h.attributes, MathML.h.scope, true, Seq(): _*) {

	def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException("Applyables should not get evaled, use eval on the surrounding apply element."))

	def cnStep: Option[Constant] = None

	def variables: Set[String] = Set()
	
	def simplifyStep() : this.type = this

	def derivative(wrt: String): MathMLElem = throw new UnsupportedOperationException("Applyables should not get derived, use derive on the surrounding apply element.")
}

object Plus extends Operator("plus")

object Minus extends Operator("minus")

object Times extends Operator("times")

object Divide extends Operator("divide")

object Power extends Operator("power")

object Log extends Operator("log")

object Ln extends Operator("ln")