package mathml.scalar

import scala.util._
import scala.xml._
import mathml._

case class Cn(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val value: Node)
	extends MathMLElem(prefix, "cn", attributes1, scope, minimizeEmpty, Seq(Cn.format(value)): _*) {

	def this(value: Node) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, Cn.format(value))

	def eval(boundVariables: Map[String, Double]) = Try(text.toDouble)

	def isZero = Try(text.trim().toDouble).getOrElse(Double.NaN) == 0d

	def isOne = Try(text.trim().toDouble).getOrElse(Double.NaN) == 1d

	def simplify() = this

	def variables: Set[String] = Set()

	def derivative(wrt: String) = Cn(0)
}

object Cn {
	private def format(v : Node) : Text = format(v.text)
	
	private def format(v : String) = Text(v.trim) // TODO fail if string is not a number
	
	def apply(value: Node) = new Cn(format(value))
	
	def apply(value: Any) = new Cn(format(value.toString))
}





case class CnReal(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val value: Node)
	extends MathMLElem(prefix, "cn", new UnprefixedAttribute("type", Seq(Text("real")), null), scope, minimizeEmpty, Seq(): _*) {

	def this(value: Node) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, value)

	def eval(boundVariables: Map[String, Double]) = Try(text.toDouble)

	def isZero = Try(text.trim().toDouble).getOrElse(Double.NaN) == 0d

	def isOne = Try(text.trim().toDouble).getOrElse(Double.NaN) == 1d

	def simplify() = this

	def variables: Set[String] = Set()

	def derivative(wrt: String) = Cn(0)
}






class NumberText[T](val value: T) extends Text(value.toString)

case class RealText(override val value: BigDecimal) extends NumberText[BigDecimal](value)

case class IntegerText(override val value: BigInt) extends NumberText[BigInt](value)


