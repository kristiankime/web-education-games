package mathml.scalar.apply

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.concept._

case class ApplyLog(base : BigDecimal, value: MathMLElem) extends Logarithm(base, value, Seq(Log, Logbase(base)): _*) {

	def simplifyStep() = ApplyLog(b, v.s)

	def derivative(x: String) = {
		val f = v.s
		val fP = f.d(x)
		val log_b = Cn(math.log(b.doubleValue))
		
		fP / (log_b * f)
	}
	
}