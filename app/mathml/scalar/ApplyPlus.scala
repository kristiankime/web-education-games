package mathml.scalar

import scala.util._
import scala.xml._
import mathml._

case class ApplyPlus(val values: MathMLElem*)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq[MathMLElem](Plus()) ++ values): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(values.map(_.eval(boundVariables).get).reduceLeft(_ + _))

	def cn: Option[Cn] = if (values.forall(_.cn.nonEmpty)) {
		Some(values.map(_.cn.get).reduce(_ + _))
	} else {
		None
	}

	def variables: Set[String] = values.foldLeft(Set[String]())(_ ++ _.variables)

	def simplify() = {
		if (isZero) Cn(0)
		else if (isOne) Cn(1)
		else {
			val nonZeroVals = values.map(_.simplify).filter(!_.isZero)
			if (nonZeroVals.isEmpty) Cn(0)
			else if (nonZeroVals.size == 1) nonZeroVals(0)
			else ApplyPlus(nonZeroVals: _*)
		}
	}

	def derivative(wrt: String) = ApplyPlus(values.map(_.derivative(wrt)): _*).simplify
}