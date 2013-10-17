package mathml

import scala.util.Try
import scala.xml.MetaData
import scala.xml.NamespaceBinding
import scala.xml.Node
import scala.xml.Text


case class Cn(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val value: Node)
	extends MathMLElem(prefix, "cn", attributes1, scope, minimizeEmpty, Seq(value): _*) {

	def this(value: Node) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, value)

	def eval(boundVariables: Map[String, Double]) = Try(text.toDouble)

	def isZero = Try(text.trim().toDouble).getOrElse(Double.MaxValue) == 0d

	def isOne = Try(text.trim().toDouble).getOrElse(Double.MaxValue) == 1d

	def simplify() = this

	def derivative(wrt: String) = Cn(0)
}

object Cn {
	def apply(value: Node) = new Cn(value)
	def apply(value: Any) = new Cn(Text(value.toString))
}
