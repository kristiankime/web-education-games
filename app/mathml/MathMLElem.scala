package mathml

import scala.xml.NamespaceBinding
import scala.xml.MetaData
import scala.xml.Node
import scala.xml.Elem
import scala.xml.Text
import scala.util.Try
import scala.util.Success
import scala.util.Failure

sealed abstract class MathMLElem(
	prefix: String,
	label: String,
	attributes1: MetaData,
	scope: NamespaceBinding,
	minimizeEmpty: Boolean,
	child: Node*)
	extends Elem(prefix, label, attributes1, scope, minimizeEmpty, child: _*) {

	def eval(boundVariables: Map[String, Double]) : Try[Double]
}

case class Apply(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val applyFunction: Applyable,
	val applyValues: MathMLElem*)
	extends MathMLElem(prefix, "apply", attributes1, scope, minimizeEmpty, (Seq[MathMLElem](applyFunction) ++ applyValues): _*) {

	def this(applyFunction: Applyable, applyValues: MathMLElem*) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, applyFunction, applyValues: _*)

	def eval(boundVariables: Map[String, Double]) = applyFunction.applyEval(boundVariables, applyValues)
}

object Apply {
	def apply(applyFunction: Applyable, applyValues: MathMLElem*) = new Apply(applyFunction, applyValues: _*)
}

// Applyables (Things that can be the first element in a apply element, i.e. plus, minus, mult etc...) 
abstract class Applyable(
	override val prefix: String,
	override val label: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean)
	extends MathMLElem(prefix, label, attributes1, scope, minimizeEmpty, Seq(): _*) {

	def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException("Applyables should not get evaled, use applyEval instead"))

	def applyEval(boundVariables: Map[String, Double], parameters: Seq[MathMLElem]): Try[Double]
}

case class Plus(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean)
	extends Applyable(prefix, "plus", attributes1, scope, minimizeEmpty) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false)

	def applyEval(boundVariables: Map[String, Double], parameters: Seq[MathMLElem]) = Try(parameters.map(_.eval(boundVariables).get).reduceLeft(_ + _))
}

object Plus {
	def apply() = new Plus()
}

case class Minus(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean)
	extends Applyable(prefix, "minus", attributes1, scope, minimizeEmpty) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false)

	def applyEval(boundVariables: Map[String, Double], parameters: Seq[MathMLElem]) = Try(parameters.map(_.eval(boundVariables).get).reduceLeft(_ - _))
}

object Minus {
	def apply() = new Minus()
}

case class Times(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean)
	extends Applyable(prefix, "times", attributes1, scope, minimizeEmpty) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false)

	def applyEval(boundVariables: Map[String, Double], parameters: Seq[MathMLElem]) = Try(parameters.map(_.eval(boundVariables).get).reduceLeft(_ * _))
}

object Times {
	def apply() = new Times()
}

case class Divide(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean)
	extends Applyable(prefix, "divide", attributes1, scope, minimizeEmpty) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false)

	def applyEval(boundVariables: Map[String, Double], parameters: Seq[MathMLElem]) = Try(parameters.map(_.eval(boundVariables).get).reduceLeft(_ / _))
}

object Divide {
	def apply() = new Divide()
}

case class Power(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean)
	extends Applyable(prefix, "power", attributes1, scope, minimizeEmpty) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false)
	
	def applyEval(boundVariables: Map[String, Double], parameters: Seq[MathMLElem]) = Try(parameters.map(_.eval(boundVariables).get).reduceLeft(Math.pow(_ , _)))
}

object Power {
	def apply() = new Power()
}

// Terminal Identifiers
case class Cn(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val value: Node)
	extends MathMLElem(prefix, "cn", attributes1, scope, minimizeEmpty, Seq(value): _*) {

	def this(value: Node) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, value)
	
	def eval(boundVariables: Map[String, Double]) = Try(text.toDouble)
}

object Cn {
	def apply(value: Node) = new Cn(value)
	def apply(value: String) = new Cn(Text(value))
	def apply(value: Double) = new Cn(Text(value.toString))
}

case class Ci(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val value: Node)
	extends MathMLElem(prefix, "ci", attributes1, scope, minimizeEmpty, Seq(value): _*) {

	def this(value: Node) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, value)
	
	def eval(boundVariables: Map[String, Double]) = Try(boundVariables.get(text).get)
}

object Ci {
	def apply(value: Node) = new Ci(value)
	def apply(value: String) = new Ci(Text(value))
}