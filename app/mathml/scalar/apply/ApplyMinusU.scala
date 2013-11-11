package mathml.scalar.apply

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept.Constant

/**
 * ApplyMinusUnary
 */
case class ApplyMinusU(val value: MathMLElem)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq[MathMLElem](Minus) ++ value): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(-1d * value.eval(boundVariables).get)

	def cnStep: Option[Constant] = value.cnStep match {
		case Some(v) => Some(v * Cn(-1))
		case _ => None
	}

	def simplifyStep() =
		if (cnStep.nonEmpty) cnStep.get
		else ApplyMinusU(value.simplifyStep)

	def variables: Set[String] = value.variables

	def derivative(wrt: String) = ApplyMinusU(value.d(wrt).s).s
}