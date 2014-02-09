package mathml.scalar.apply.trig

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept._

case class ApplySec(value: MathMLElem) extends UnaryFunction(value, Sec) {

	override def eval(b: Map[String, Double]) = Try(Trigonometry.sec(v.eval(b).get))

	override def constant: Option[Constant] = v.c match {
		case Some(v) => Some(Trigonometry.sec(v))
		case _ => None
	}

	def simplifyStep() = ApplySec(v.s)

	def derivative(x: String) = ApplyTan(v.s) * ApplySec(v.s)* v.d(x)

}