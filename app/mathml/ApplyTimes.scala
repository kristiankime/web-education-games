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
		else {
			val nonOneVals = values.map(_.simplify).filter(!_.isOne)
			if(nonOneVals.isEmpty) Cn(1)
			else if(nonOneVals.size == 1) nonOneVals(0)
			else ApplyTimes(prefix, attributes1, scope, minimizeEmpty, times, nonOneVals: _*)
		}
		
		//		else if (values.size == 1) values(0).simplify
//		else ApplyTimes(prefix, attributes1, scope, minimizeEmpty, times, values.map(_.simplify).filter(!_.isOne): _*)
	}

	def derivative(wrt: String): MathMLElem = values.reduce((a, b) => productRule(wrt, a, b)).simplify

	private def productRule(wrt: String, val1: MathMLElem, val2: MathMLElem) =
		ApplyPlus(
				ApplyTimes(val1.derivative(wrt), val2), 
				ApplyTimes(val1, val2.derivative(wrt))
				).simplify
	
}

object ApplyTimes {
	def apply(times: Times, values: MathMLElem*) = new ApplyTimes(times, values: _*)
	def apply(values: MathMLElem*) = new ApplyTimes(Times(), values: _*)
}