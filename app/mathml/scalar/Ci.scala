package mathml.scalar

import scala.util._
import scala.xml._
import mathml._

case class Ci(val identifier: IdentifierText)
	extends MathMLElem(MathML.h.prefix, "ci", MathML.h.attributes, MathML.h.scope, false, Seq(identifier): _*) {

	override val c = None;

	override val s = this;

	def eval(boundVariables: Map[String, Double]) = Try(boundVariables.get(text).get)

	def cnStep: Option[Cn] = None

	def simplifyStep() = this

	def variables: Set[String] = Set(identifier.id)

	def derivative(wrt: String): MathMLElem = if (text.trim == wrt) Cn(1) else Cn(0)
}

object Ci {
	def apply(value: String) = new Ci(IdentifierText(value))
}

case class IdentifierText(val id: String) extends Text(id.trim)