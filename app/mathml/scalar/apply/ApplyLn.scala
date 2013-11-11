package mathml.scalar.apply

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept.Constant

case class ApplyLn(val value: MathMLElem)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq[MathMLElem](Ln) ++ value): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(math.log(value.eval(boundVariables).get))

	def cnStep: Option[Constant] = value.cnStep match {
		case Some(v) => Some(v.ln)
		case _ => None
	}

	def simplifyStep() =
		if (cnStep.nonEmpty) cnStep.get
		else ApplyMinusU(value.simplifyStep)

	def variables: Set[String] = value.variables

	def derivative(wrt: String) = ApplyMinusU(value.d(wrt).s).s
}