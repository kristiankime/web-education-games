package mathml.scalar.apply

import scala.util._
import scala.xml._
import mathml._
import mathml.scalar._
import mathml.scalar.Root
import mathml.scalar.concept._

case class ApplyRoot(degree: BigDecimal, value: MathMLElem) extends NthRoot(degree, value, Seq(Root, Degree(degree)): _*) {

	def simplifyStep() = {if (degree == ApplyRoot.BD_2) { ApplySqrt(v.s) } else { ApplyRoot(degree, v.s) }}

	def derivative(x: String) = {
		import ApplyRoot._

		val f = v.s
		val fP = f.d(x)

		// from power rule
		val c = Cn(degree)
		//      1          1 - d             d
		// n =  - - 1  =>  -----   => -1 * -----
		//      d            d             1 - d
		val n = BD_n1 * (degree / (BD_1 - degree))

		fP / (c * ApplyRoot(n, f))
	}

}

object ApplyRoot {
	private val BD_1 = BigDecimal(1)
	private val BD_2 = BigDecimal(2)
	private val BD_n1 = BigDecimal(-1)

	def apply(value: MathMLElem): ApplyRoot = ApplyRoot(value)
}