package mathml.scalar.concept

import mathml.scalar._

object Trigonometry {

	def sin(value: Constant) = value match {
		case c: ConstantInteger => Cn(math.sin(c.v.doubleValue))
		case c: ConstantDecimal => Cn(math.sin(c.v.doubleValue))
	}

	def cos(value: Constant) = value match {
		case c: ConstantInteger => Cn(math.cos(c.v.doubleValue))
		case c: ConstantDecimal => Cn(math.cos(c.v.doubleValue))
	}
	
	def tan(value: Constant) = value match {
		case c: ConstantInteger => Cn(math.tan(c.v.doubleValue))
		case c: ConstantDecimal => Cn(math.tan(c.v.doubleValue))
	}
	
	def sec(value: Constant) = value match {
		case c: ConstantInteger => Cn(1d / math.sin(c.v.doubleValue))
		case c: ConstantDecimal => Cn(1d / math.sin(c.v.doubleValue))
	}

	def csc(value: Constant) = value match {
		case c: ConstantInteger => Cn(1d / math.cos(c.v.doubleValue))
		case c: ConstantDecimal => Cn(1d / math.cos(c.v.doubleValue))
	}
	
	def cot(value: Constant) = value match {
		case c: ConstantInteger => Cn(1d / math.tan(c.v.doubleValue))
		case c: ConstantDecimal => Cn(1d / math.tan(c.v.doubleValue))
	}
}