package mathml

import scala.util.Try
import scala.xml.MetaData
import scala.xml.NamespaceBinding

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

	def isZero = values.map(_.simplify).foldLeft(false)(_ || _.isZero)

	def isOne = values.map(_.simplify).foldLeft(true)(_ && _.isOne)

	def simplify() = {
		if (isZero) Cn(0)
		else if (isOne) Cn(1)
		else ApplyTimes(prefix, attributes1, scope, minimizeEmpty, times, values.map(_.simplify).filter(_.isOne): _*)
	}

	def derivative(wrt: String): Option[MathMLElem] = {
		values.map(Option(_)).reduce((a, b) => productRule(wrt, a.get, b.get))
	}

	private def productRule(wrt: String, val1: MathMLElem, val2: MathMLElem) = {
		(val1.derivative(wrt), val2.derivative(wrt)) match {
			case (None, None) => None
			case (Some(der1), None) => Some(ApplyTimes(der1, val2))
			case (None, Some(der2)) => Some(ApplyTimes(val1, der2))
			case (Some(der1), Some(der2)) => Some(ApplyPlus(ApplyTimes(der1, val2), ApplyTimes(Times(), val1, der2)))
		}
	}
}

object ApplyTimes {
	def apply(times: Times, values: MathMLElem*) = new ApplyTimes(times, values: _*)
	def apply(values: MathMLElem*) = new ApplyTimes(Times(), values: _*)
}