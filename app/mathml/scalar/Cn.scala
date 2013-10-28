package mathml.scalar

import scala.util._
import scala.xml._
import mathml._
import scala.math.ScalaNumber
import scala.math.Numeric

// LATER might be able to make Cn scala.math.Numeric 
sealed abstract class Cn(attributes1: MetaData, val value: NumberText[_ <: ScalaNumber])
	extends MathMLElem(MathML.h.prefix, "cn", attributes1, MathML.h.scope, false, Seq(value): _*) {

	def eval(boundVariables: Map[String, Double]) = Try(value.num.doubleValue)

	def cn: Option[this.type] = Some(this)
	
	def simplify(): this.type = this

	def variables: Set[String] = Set()

	def derivative(wrt: String) = Cn(0)
	
	def +(m : Cn): Cn 

	def *(m : Cn): Cn

	def -(m : Cn): Cn

	def /(m : Cn): Cn

	def ^(m : Cn): Cn
	
}

object Cn {
	def apply(value: String): Try[Cn] = {
		(Try(BigInt(value)), Try(BigDecimal(value))) match {
			case (Success(v), _) => Success(Cn(v))
			case (_, Success(v)) => Success(Cn(v))
			case (Failure(a), Failure(b)) => Failure(b)
		}
	}

	def apply(value: Node): Try[Cn] = apply(value.text.trim)

	def apply(value: Short) = CnInteger(IntegerText(value))

	def apply(value: Int) = CnInteger(IntegerText(value))

	def apply(value: Long) = CnInteger(IntegerText(value))

	def apply(value: BigInt) = CnInteger(IntegerText(value))

	def apply(value: Float) = CnReal(RealText(value))

	def apply(value: Double) = CnReal(RealText(value))
	
	def apply(value: BigDecimal) = CnReal(RealText(value))

	val realType = <cn type="real"></cn>.attributes //new UnprefixedAttribute("type", Seq(Text("real")), null)

	val integerType = <cn type="integer"></cn>.attributes //new UnprefixedAttribute("type", Seq(Text("integer")), null)
}

case class CnInteger(override val value: IntegerText) extends Cn(Cn.integerType, value) {

	def isZero = value.num.compare(BigInt(0)) == 0

	def isOne = value.num.compare(BigInt(1)) == 0
	
	def +(m : Cn) = m match {
		case v:CnInteger => Cn(value.num + v.value.num)
		case v:CnReal => Cn(BigDecimal(value.num) + v.value.num)
	}

	def *(m : Cn) = m match {
		case v:CnInteger => Cn(value.num * v.value.num)
		case v:CnReal => Cn(BigDecimal(value.num) * v.value.num)
	}

	def -(m : Cn) = m match {
		case v:CnInteger => Cn(value.num - v.value.num)
		case v:CnReal => Cn(BigDecimal(value.num) - v.value.num)
	}

	def /(m : Cn) = m match {
		case v:CnInteger => Cn(value.num / v.value.num)
		case v:CnReal => Cn(BigDecimal(value.num) / v.value.num)
	}

	def ^(m : Cn) = m match {
		case v:CnInteger => Cn(value.num ^ v.value.num)
		case v:CnReal => Cn(math.pow(value.num.doubleValue, v.value.num.doubleValue))
	}
}

case class CnReal(override val value: RealText) extends Cn(Cn.realType, value) {

	def isZero = value.num.compare(BigDecimal(0)) == 0

	def isOne = value.num.compare(BigDecimal(1)) == 0
	
	def +(m : Cn) = m match {
		case v:CnInteger => Cn(value.num + BigDecimal(v.value.num))
		case v:CnReal => Cn(value.num + v.value.num)
	}

	def *(m : Cn) = m match {
		case v:CnInteger => Cn(value.num * BigDecimal(v.value.num))
		case v:CnReal => Cn(value.num * v.value.num)
	}

	def -(m : Cn) = m match {
		case v:CnInteger => Cn(value.num - BigDecimal(v.value.num))
		case v:CnReal => Cn(value.num - v.value.num)
	}

	def /(m : Cn) = m match {
		case v:CnInteger => Cn(value.num / BigDecimal(v.value.num))
		case v:CnReal => Cn(value.num / v.value.num)
	}

	def ^(m : Cn) = m match {
		case v:CnInteger => Cn(math.pow(value.num.doubleValue, v.value.num.doubleValue))
		case v:CnReal => Cn(math.pow(value.num.doubleValue, v.value.num.doubleValue))
	}
}


class NumberText[T <: ScalaNumber](val num: T) extends Text(num.toString)

case class RealText(override val num: BigDecimal) extends NumberText[BigDecimal](num)

case class IntegerText(override val num: BigInt) extends NumberText[BigInt](num)


