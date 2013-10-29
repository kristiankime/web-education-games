package mathml.scalar

import scala.util._
import scala.xml._
import mathml._

/**
 * ApplyMinus for the Binary case
 */
case class ApplyMinusB(val value1: MathMLElem, val value2: MathMLElem)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq[MathMLElem](Minus()) ++ value1 ++ value2): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(value1.eval(boundVariables).get - value2.eval(boundVariables).get)

	def cn: Option[Cn] = (value1.cn, value2.cn) match {
		case (Some(v1), Some(v2)) => Some(v1 - v2)
		case _ => None
	}

	def simplify() =
		if (cn.nonEmpty) cn.get
		else if (value2.isZero) value1.simplify
		else if (value1.isZero) ApplyMinusU(value2).simplify
		else ApplyMinusB(value1.simplify, value2.simplify)

	def variables: Set[String] = value1.variables ++ value2.variables

	def derivative(wrt: String): MathMLElem = (ApplyMinusB(value1.derivative(wrt), value2.derivative(wrt))).simplify
}