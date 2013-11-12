package mathml.scalar.apply

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept.Constant

/**
 * ApplyMinus for the Binary case
 */
case class ApplyMinusB(val value1: MathMLElem, val value2: MathMLElem)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq[MathMLElem](Minus) ++ value1 ++ value2): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(value1.eval(boundVariables).get - value2.eval(boundVariables).get)

	def cnStep: Option[Constant] = (value1.c, value2.c) match {
		case (Some(v1), Some(v2)) => Some(v1 - v2)
		case _ => None
	}

	def simplifyStep() =
		if (c.nonEmpty) c.get
		else if (value2.isZero) value1.s
		else if (value1.isZero) -(value2.s)
		else value1.s - value2.s

	def variables: Set[String] = value1.variables ++ value2.variables

	def derivative(wrt: String): MathMLElem = ( value1.d(wrt).s - value2.d(wrt).s).s
}