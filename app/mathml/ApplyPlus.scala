package mathml

import scala.xml.NamespaceBinding
import scala.xml.MetaData
import scala.util.Try

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

	def isZero = values.map(_.simplify).foldLeft(true)(_ && _.isZero)

	def isOne = {
		val simplified = values.map(_.simplify)
		val ones = simplified.filter(_.isOne).size
		val zeros = simplified.filter(_.isZero).size
		(ones == 1 && (ones + zeros == values.length))
	}

	def simplify() = {
		if (isZero) Cn(0)
		else if (isOne) Cn(1)
		else {
			val nonZeroVals = values.map(_.simplify).filter(_.isZero)
			if(nonZeroVals.isEmpty) Cn(0)
			else if(nonZeroVals.size == 1) nonZeroVals(0)
			else ApplyPlus(prefix, attributes1, scope, minimizeEmpty, plus, nonZeroVals: _*)
		}
	}

	def derivative(wrt: String) = ApplyPlus(prefix, attributes1, scope, minimizeEmpty, plus, values.map(_.derivative(wrt)):_*).simplify
}

object ApplyPlus {
	def apply(plus: Plus, applyValues: MathMLElem*) = new ApplyPlus(plus, applyValues: _*)
	def apply(applyValues: MathMLElem*) = new ApplyPlus(Plus(), applyValues: _*)
}