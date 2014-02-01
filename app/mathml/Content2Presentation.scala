package mathml

import mathml.scalar.MathMLElem

object Content2Presentation {

	def apply(content: MathMLElem) = XSLTransform(content, Ctop.str).get
		
}