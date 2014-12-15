package models.question

import com.artclod.mathml.scalar.MathMLElem

trait MathJSMathML {
	val mathML: MathMLElem
	val rawStr: String
}