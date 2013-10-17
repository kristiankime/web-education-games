package mathml

import scala.util.Try
import scala.xml.MetaData
import scala.xml.NamespaceBinding
import scala.xml.Node
import scala.xml.Text

case class Ci(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val value: Node)
	extends MathMLElem(prefix, "ci", attributes1, scope, minimizeEmpty, Seq(value): _*) {

	def this(value: Node) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, value)

	def eval(boundVariables: Map[String, Double]) = Try(boundVariables.get(text).get)

	def isZero = false

	def isOne = false

	def simplify() = this

	def derivative(wrt: String): MathMLElem = if (text == wrt) Cn(1) else Cn(0)
}

object Ci {
	def apply(value: Node) = new Ci(value)
	def apply(value: String) = new Ci(Text(value))
}