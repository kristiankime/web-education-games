package mathml.scalar.apply.trig

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept._
import mathml.scalar._

case class ApplyCos(value: MathMLElem) extends UnaryFunction(value, Cos) {

	override def eval(b: Map[String, Double]) = Try(math.cos(v.eval(b).get))

	override def cnStep: Option[Constant] = v.c match {
		case Some(v) => Some(Trigonometry.cos(v))
		case _ => None
	}

	def simplifyStep() = ApplyCos(v.s)

	def derivative(x: String) = -ApplySin(v.s) * v.d(x)

}