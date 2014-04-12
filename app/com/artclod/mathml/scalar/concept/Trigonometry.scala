package mathml.scalar.concept

import mathml.scalar._

object Trigonometry {

	def csc(v: Double) : Double = 1d / math.sin(v)
	
	def sec(v: Double) : Double =  1d / math.cos(v)
	
	def cot(v: Double) : Double=  1d / math.tan(v)
	
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
	
	def csc(value: Constant) : Constant = value match {
		case c: ConstantInteger => Cn(csc(c.v.doubleValue))
		case c: ConstantDecimal => Cn(csc(c.v.doubleValue))
	}
	
	def sec(value: Constant) : Constant = value match {
		case c: ConstantInteger => Cn(sec(c.v.doubleValue))
		case c: ConstantDecimal => Cn(sec(c.v.doubleValue))
	}
	
	def cot(value: Constant) : Constant = value match {
		case c: ConstantInteger => Cn(cot(c.v.doubleValue))
		case c: ConstantDecimal => Cn(cot(c.v.doubleValue))
	}
}