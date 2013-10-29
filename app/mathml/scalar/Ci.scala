package mathml.scalar

import scala.util._
import scala.xml._
import mathml._


case class Ci(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val value: Node)
	extends MathMLElem(prefix, "ci", attributes1, scope, minimizeEmpty, Seq(Ci.format(value)): _*) {

	def this(value: Node) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, Ci.format(value))

	def eval(boundVariables: Map[String, Double]) = Try(boundVariables.get(text).get)

	def cnStep: Option[Cn] = None
	
	def simplifyStep() = this

	def variables: Set[String] = Set(value.text.trim)
	
	def derivative(wrt: String): MathMLElem = if (text.trim == wrt) Cn(1) else Cn(0)
}

object Ci {
	private def format(v : Node) : Text = format(v.text)
	
	private def format(v : String) = Text(v.trim) // TODO fail if string is not a valid id

	def apply(value: Node) = new Ci(format(value))

	def apply(value: String) = new Ci(format(value))
}