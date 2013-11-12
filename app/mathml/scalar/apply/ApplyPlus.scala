package mathml.scalar.apply

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept.Constant

case class ApplyPlus(val values: MathMLElem*)
	extends MathMLElem(MathML.h.prefix, "apply", MathML.h.attributes, MathML.h.scope, false, (Seq[MathMLElem](Plus) ++ values): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(values.map(_.eval(boundVariables).get).reduceLeft(_ + _))

	def cnStep: Option[Constant] =
		if (values.forall(_.c.nonEmpty)) {
			Some(values.map(_.c.get).reduce(_ + _))
		} else {
			None
		}

	def simplifyStep() = {
		val cns = values.map(_.c).filter(_.nonEmpty).map(_.get)
		val elems = values.filter(_.c.isEmpty)
		(cns, elems) match {
			case (Seq(cns @ _*), Seq()) => cns.reduce(_ + _)
			case (Seq(), Seq(elem)) => elem
			case (Seq(), Seq(elems @ _*)) => this
			case (Seq(cns @ _*), Seq(elems @ _*)) => ApplyPlus(elems ++ Seq(cns.reduce(_ + _)): _*)
		}
	}

	def variables: Set[String] = values.foldLeft(Set[String]())(_ ++ _.variables)

	def derivative(wrt: String) = ApplyPlus(values.map(_.d(wrt).s): _*).s
}