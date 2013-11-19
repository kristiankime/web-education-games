package mathml.scalar.apply.trig

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept._

case class ApplyCsc(value: MathMLElem) extends UnaryFunction(value, Csc) {

	override def eval(b: Map[String, Double]) = Try(Trigonometry.csc(v.eval(b).get))

	override def cnStep: Option[Constant] = v.c match {
		case Some(v) => Some(Trigonometry.csc(v))
		case _ => None
	}

	def simplifyStep() = ApplyCsc(v.s)

	def derivative(x: String) = -ApplyCot(v.s) * ApplyCsc(v.s) * v.d(x)

}