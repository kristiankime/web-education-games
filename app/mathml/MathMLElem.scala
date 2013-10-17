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

	def eval(boundVariables: Map[String, Double]): Try[Double]
}

case class ApplyPlus(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val plus: Plus,
	val values: MathMLElem*)
	extends MathMLElem(prefix, "apply", attributes1, scope, minimizeEmpty, (Seq[MathMLElem](plus) ++ values): _*) {

	def this(plus: Plus, applyValues: MathMLElem*) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, plus, applyValues: _*)

	def eval(boundVariables: Map[String, Double]) = Try(values.map(_.eval(boundVariables).get).reduceLeft(_ + _))
}

object ApplyPlus {
	def apply(plus: Plus, applyValues: MathMLElem*) = new ApplyPlus(plus, applyValues: _*)
	def apply(applyValues: MathMLElem*) = new ApplyPlus(Plus(), applyValues: _*)
}

case class ApplyMinusUnary(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val minus: Minus,
	val value: MathMLElem)
	extends MathMLElem(prefix, "apply", attributes1, scope, minimizeEmpty, (Seq[MathMLElem](minus) ++ value): _*) {

	def this(minus: Minus, value: MathMLElem) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, minus, value)

	def eval(boundVariables: Map[String, Double]) = Try(-1d * value.eval(boundVariables).get)
}

object ApplyMinusUnary {
	def apply(minus: Minus, value: MathMLElem) = new ApplyMinusUnary(minus, value)
	def apply(value: MathMLElem) = new ApplyMinusUnary(Minus(), value)
}

case class ApplyMinusBinary(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val minus: Minus,
	val value1: MathMLElem,
	val value2: MathMLElem)
	extends MathMLElem(prefix, "apply", attributes1, scope, minimizeEmpty, (Seq[MathMLElem](minus) ++ value1 ++ value2): _*) {

	def this(minus: Minus, value1: MathMLElem, value2: MathMLElem) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, minus, value1, value2)

	def eval(boundVariables: Map[String, Double]) = Try(value1.eval(boundVariables).get - value2.eval(boundVariables).get)
}

object ApplyMinusBinary {
	def apply(minus: Minus, value1: MathMLElem, value2: MathMLElem) = new ApplyMinusBinary(minus, value1, value2)
	def apply(value1: MathMLElem, value2: MathMLElem) = new ApplyMinusBinary(Minus(), value1, value2)
}

case class ApplyTimes(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val times: Times,
	val values: MathMLElem*)
	extends MathMLElem(prefix, "apply", attributes1, scope, minimizeEmpty, (Seq[MathMLElem](times) ++ values): _*) {

	def this(times: Times, applyValues: MathMLElem*) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, times, applyValues: _*)

	def eval(boundVariables: Map[String, Double]) = Try(values.map(_.eval(boundVariables).get).reduceLeft(_ * _))
}

object ApplyTimes {
	def apply(times: Times, values: MathMLElem*) = new ApplyTimes(times, values: _*)
	def apply(values: MathMLElem*) = new ApplyTimes(Times(), values: _*)
}

case class ApplyDivide(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val divide: Divide,
	val numerator: MathMLElem,
	val denominator: MathMLElem)
	extends MathMLElem(prefix, "apply", attributes1, scope, minimizeEmpty, (Seq[MathMLElem](divide) ++ numerator ++ denominator): _*) {

	def this(divide: Divide, numerator: MathMLElem, denominator: MathMLElem) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, divide, numerator, denominator)

	def eval(boundVariables: Map[String, Double]) = Try(numerator.eval(boundVariables).get / denominator.eval(boundVariables).get)
}

object ApplyDivide {
	def apply(divide: Divide, numerator: MathMLElem, denominator: MathMLElem) = new ApplyDivide(divide, numerator, denominator)
	def apply(numerator: MathMLElem, denominator: MathMLElem) = new ApplyDivide(Divide(), numerator, denominator)
}

case class ApplyPower(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean,
	val power: Power,
	val value1: MathMLElem,
	val value2: MathMLElem)
	extends MathMLElem(prefix, "apply", attributes1, scope, minimizeEmpty, (Seq[MathMLElem](power) ++ value1 ++ value2): _*) {

	def this(power: Power, value1: MathMLElem, value2: MathMLElem) = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false, power, value1, value2)

	def eval(boundVariables: Map[String, Double]) = Try(Math.pow(value1.eval(boundVariables).get, value2.eval(boundVariables).get))
}

object ApplyPower {
	def apply(power: Power, value1: MathMLElem, value2: MathMLElem) = new ApplyPower(power, value1, value2)
	def apply(value1: MathMLElem, value2: MathMLElem) = new ApplyPower(Power(), value1, value2)
}

abstract class Applyable(
	override val prefix: String,
	override val label: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean)
	extends MathMLElem(prefix, label, attributes1, scope, minimizeEmpty, Seq(): _*) {

	def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException("Applyables should not get evaled, use eval on the surrounding apply element."))
}

case class Plus(
	override val prefix: String,
	attributes1: MetaData,
	override val scope: NamespaceBinding,
	override val minimizeEmpty: Boolean)
	extends Applyable(prefix, "plus", attributes1, scope, minimizeEmpty) {

	def this() = this(MathML.h.prefix, MathML.h.attributes, MathML.h.scope, false)
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
}

object Power {
	def apply() = new Power()
}

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