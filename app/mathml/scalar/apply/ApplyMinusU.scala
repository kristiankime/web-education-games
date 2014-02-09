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

	def constant: Option[Constant] = value.c match {
		case Some(v) => Some(v * Cn(-1))
		case _ => None
	}

	def simplifyStep() = -(value.s)

	def variables: Set[String] = value.variables

	def derivative(x: String) = -(value.d(x))
}