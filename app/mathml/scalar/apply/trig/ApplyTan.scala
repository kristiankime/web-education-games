package mathml.scalar.apply.trig

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept._

case class ApplyTan(value: MathMLElem) extends UnaryFunction(value, Tan) {

	override def eval(b: Map[String, Double]) = Try(math.tan(v.eval(b).get))

	override def cnStep: Option[Constant] = v.c match {
		case Some(v) => Some(Trigonometry.tan(v))
		case _ => None
	}

	def simplifyStep() = ApplyTan(v.s)

	def derivative(x: String) = (ApplySec(v.s) ^ `2`) * v.d(x)

}