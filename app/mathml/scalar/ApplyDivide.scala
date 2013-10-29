package mathml.scalar

import scala.util._
import scala.xml._
import mathml._

case class ApplyDivide(val numerator: MathMLElem, val denominator: MathMLElem)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq[MathMLElem](Divide()) ++ numerator ++ denominator): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(numerator.eval(boundVariables).get / denominator.eval(boundVariables).get)

	def cnStep: Option[Cn] = (numerator.cnStep, denominator.cnStep) match {
		case (Some(nu), Some(de)) => Some(nu / de)
		case (Some(nu), _) => if (nu == Cn(0)) Some(nu) else None
		case _ => None
	}

	def simplifyStep() =
		if (cnStep.nonEmpty) cnStep.get
		else ApplyDivide(numerator.simplifyStep, denominator.simplifyStep)

	def variables: Set[String] = numerator.variables ++ denominator.variables

	// User the quotient rule (http://en.wikipedia.org/wiki/Quotient_rule)
	def derivative(wrt: String): MathMLElem = {
		val f = numerator
		val fP = f.d(wrt).simplifyStep
		val g = denominator
		val gP = g.d(wrt).simplifyStep

		// (f/g)' = (f'g - g'f)/g^2
		(fP * g - gP * f) / g ^ Cn(2) simplifyStep
	}
}