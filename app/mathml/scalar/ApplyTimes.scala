package mathml.scalar

import scala.util._
import scala.xml._
import mathml._

case class ApplyTimes(val values: MathMLElem*)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq[MathMLElem](Times()) ++ values): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(values.map(_.eval(boundVariables).get).reduceLeft(_ * _))

	def cnStep: Option[Cn] = if (values.forall(_.cnStep.nonEmpty)) {
		Some(values.map(_.cnStep.get).reduce(_ * _))
	} else {
		None
	}

	def simplifyStep() = {
		val cns = values.map(_.cnStep).filter(_.nonEmpty).map(_.get)
		val elems = values.filter(_.cnStep.isEmpty)
		(cns, elems) match {
			case (Seq(cns @ _*), Seq()) => cns.reduce(_ * _)
			case (Seq(), Seq(elem)) => elem
			case (Seq(), Seq(elems @ _*)) => this
			case (Seq(cns @ _*), Seq(elems @ _*)) => ApplyTimes(Seq(cns.reduce(_ * _)) ++ elems: _*)
		}
	}

	def variables: Set[String] = values.foldLeft(Set[String]())(_ ++ _.variables)

	def derivative(wrt: String): MathMLElem = values.reduce((a, b) => productRule(wrt, a, b)).simplifyStep

	// http://en.wikipedia.org/wiki/Product_rule
	// (f*g)' = f'*g + f*g'
	private def productRule(wrt: String, f: MathMLElem, g: MathMLElem) = {
		val fP = f.d(wrt).simplifyStep
		val gP = g.d(wrt).simplifyStep
		fP * g + f * gP
	}
}
