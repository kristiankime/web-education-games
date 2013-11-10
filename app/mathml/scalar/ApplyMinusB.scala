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

	def cnStep: Option[Constant] = (value1.cnStep, value2.cnStep) match {
		case (Some(v1), Some(v2)) => Some(v1 - v2)
		case _ => None
	}

	def simplifyStep() =
		if (cnStep.nonEmpty) cnStep.get
		else if (value2.isZero) value1.simplifyStep
		else if (value1.isZero) (ApplyMinusU(value2)).simplifyStep
		else ApplyMinusB(value1.simplifyStep, value2.simplifyStep)

	def variables: Set[String] = value1.variables ++ value2.variables

	def derivative(wrt: String): MathMLElem = (ApplyMinusB(value1.d(wrt).s, value2.d(wrt).s)).s
}