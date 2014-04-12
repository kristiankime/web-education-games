package mathml.scalar.apply.trig

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept._

case class ApplyCot(value: MathMLElem) extends UnaryFunction(value, Cot) {

	override def eval(b: Map[String, Double]) = Try(Trigonometry.cot(v.eval(b).get))

	override def constant: Option[Constant] = v.c match {
		case Some(v) => Some(Trigonometry.cot(v))
		case _ => None
	}

	def simplifyStep() = ApplyCot(v.s)

	def derivative(x: String) = (-ApplyCsc(v) ^ `2`) * v.d(x)

}