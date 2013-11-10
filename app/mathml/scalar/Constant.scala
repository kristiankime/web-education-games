package mathml.scalar

import scala.math.ScalaNumber
import mathml.MathML
import scala.xml.MetaData
import scala.util.Try
import scala.xml.Node

// LATER might be able to make Constant scala.math.Numeric 
abstract class Constant(name: String, attributes1: MetaData, minimizeEmpty: Boolean, val v: Any, override val child: Node*)
	extends MathMLElem(MathML.h.prefix, name, attributes1, MathML.h.scope, minimizeEmpty, child: _*) {
	
	def cnStep: Option[this.type] = Some(this)

	def simplifyStep(): this.type = this

	def variables: Set[String] = Set()

	def derivative(wrt: String) = `0`

	def +(c: Constant): Constant

	def *(c: Constant): Constant

	def -(c: Constant): Constant

	def /(c: Constant): Constant

	def ^(c: Constant): Constant
}

class ConstantInteger(name: String, attributes1: MetaData, minimizeEmpty: Boolean, override val v: BigInt, override val child: Node*)
	extends Constant(name, attributes1, minimizeEmpty, v, child: _*) {

	def eval(boundVariables: Map[String, Double]) = Try(v.doubleValue)

	def +(c: Constant) = c match {
		case m: ConstantInteger => Cn(v + m.v)
		case m: ConstantDecimal => Cn(BigDecimal(v) + m.v)
	}

	def *(c: Constant) = c match {
		case m: ConstantInteger => Cn(v * m.v)
		case m: ConstantDecimal => Cn(BigDecimal(v) * m.v)
	}

	def -(c: Constant) = c match {
		case m: ConstantInteger => Cn(v - m.v)
		case m: ConstantDecimal => Cn(BigDecimal(v) - m.v)
	}

	def /(c: Constant) = c match {
		case m: ConstantInteger => Cn(BigDecimal(v) / BigDecimal(m.v))
		case m: ConstantDecimal => Cn(BigDecimal(v) / m.v)
	}

	def ^(c: Constant) = c match {
		case m: ConstantInteger => Cn(v.pow(m.v.intValue))
		case m: ConstantDecimal => Cn(math.pow(v.doubleValue, m.v.doubleValue))
	}
}

class ConstantDecimal(name: String, attributes1: MetaData, minimizeEmpty: Boolean, override val v: BigDecimal, override val child: Node*)
	extends Constant(name, attributes1, minimizeEmpty, v, child: _*) {

	def eval(boundVariables: Map[String, Double]) = Try(v.doubleValue)

	def +(c: Constant) = c match {
		case m: ConstantInteger => Cn(v + BigDecimal(m.v))
		case m: ConstantDecimal => Cn(v + m.v)
	}

	def *(c: Constant) = c match {
		case m: ConstantInteger => Cn(v * BigDecimal(m.v))
		case m: ConstantDecimal => Cn(v * m.v)
	}

	def -(c: Constant) = c match {
		case m: ConstantInteger => Cn(v - BigDecimal(m.v))
		case m: ConstantDecimal => Cn(v - m.v)
	}

	def /(c: Constant) = c match {
		case m: ConstantInteger => Cn(v / BigDecimal(m.v))
		case m: ConstantDecimal => Cn(v / m.v)
	}

	def ^(c: Constant) = c match {
		case m: ConstantInteger => Cn(math.pow(v.doubleValue, m.v.doubleValue))
		case m: ConstantDecimal => Cn(math.pow(v.doubleValue, m.v.doubleValue))
	}
}