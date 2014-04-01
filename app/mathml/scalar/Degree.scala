package mathml.scalar

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept.Constant
import scala.util.Success

case class Degree(val value: Constant)
	extends MathMLElem(MathML.h.prefix, "degree", MathML.h.attributes, MathML.h.scope, false, Seq(value): _*) {

	def eval(boundVariables: Map[String, Double]) = Failure(new UnsupportedOperationException("Degree should not get evaled, use eval on the surrounding element."))

	def constant: Option[Constant] = None

	def variables: Set[String] = Set()

	def simplifyStep() = this

	def derivative(wrt: String): MathMLElem = throw new UnsupportedOperationException("Degree should not get derived, use derive on the surrounding element.")

	val v : BigDecimal = value match {
		case c: CnInteger => BigDecimal(c.v)
		case c: CnReal => c.v
	}
}

object Degree {

	def apply(v: BigDecimal): Degree = Degree(Cn(v))

	def apply(e: Elem): Try[Degree] = MathML(e) match {
		case Failure(a) => Failure(a)
		case Success(a) => a match {
			case c: CnInteger => Degree(Cn(c.v))
			case c: CnReal => Degree(Cn(c.v))
			case _ => Failure(new IllegalArgumentException("Could not create Degree from " + e))
		}
	}
}