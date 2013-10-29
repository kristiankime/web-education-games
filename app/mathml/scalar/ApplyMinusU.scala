package mathml.scalar

import scala.util._
import scala.xml._
import mathml._

/**
 * ApplyMinusUnary
 */
case class ApplyMinusU(val value: MathMLElem)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq[MathMLElem](Minus()) ++ value): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(-1d * value.eval(boundVariables).get)

	def cn: Option[Cn] = value.cn match {
		case Some(v) => Some(v * Cn(-1))
		case _ => None
	}

	def simplify() =
		if (cn.nonEmpty) cn.get
		else ApplyMinusU(value.simplify)

	def variables: Set[String] = value.variables

	def derivative(wrt: String) = ApplyMinusU(value.derivative(wrt)).simplify
}