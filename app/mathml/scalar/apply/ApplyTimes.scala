package mathml.scalar.apply

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept.Constant

case class ApplyTimes(val values: MathMLElem*)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq[MathMLElem](Times) ++ values): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(values.map(_.eval(boundVariables).get).reduceLeft(_ * _))

	def cnStep: Option[Constant] = if (values.forall(_.c.nonEmpty)) {
		Some(values.map(_.c.get).reduce(_ * _))
	} else if (values.map(_.c).contains(Some(`0`))) {
		Some(`0`)
	} else {
		None
	}

	def simplifyStep() = {
		if (cnStep.nonEmpty) {
			cnStep.get
		} else {
			val cns_ = values.map(_.cnStep).filter(_.nonEmpty).map(_.get)
			val elems_ = values.filter(_.cnStep.isEmpty)
			(cns_, elems_) match {
				case (Seq(cn), Seq()) => cn
				case (Seq(cns @ _*), Seq()) => cns.reduce(_ * _)
				case (Seq(), Seq(elem)) => elem
				case (Seq(), Seq(elems @ _*)) => this
				case (Seq(cn), Seq(elems @ _*)) => timesSimp(cn, elems)
				case (Seq(cns @ _*), Seq(elems @ _*)) => timesSimp(cns.reduce(_ * _), elems)
			}
		}
	}

	private def timesSimp(cn: Constant, elems: Seq[MathMLElem]) =
		(cn, elems) match {
			case (cn, Seq(elem)) if (cn.isOne) => elem
			case (cn, Seq(elems @ _*)) if (cn.isOne) => ApplyTimes(elems: _*)
			case (cn, Seq(elems @ _*)) => ApplyTimes(Seq(cn) ++ elems: _*)
		}

	def variables: Set[String] = values.foldLeft(Set[String]())(_ ++ _.variables)

	def derivative(wrt: String): MathMLElem = values.reduce((a, b) => productRule(wrt, a, b))

	// http://en.wikipedia.org/wiki/Product_rule
	// (f*g)' = f'*g + f*g'
	private def productRule(wrt: String, f: MathMLElem, g: MathMLElem) = {
		val fP = f.d(wrt).simplifyStep
		val gP = g.d(wrt).simplifyStep
		val fP_g = (fP * g).simplifyStep
		val f_gP = (f * gP).simplifyStep
		val ret = (fP_g + f_gP).simplifyStep
		ret
	}
}
