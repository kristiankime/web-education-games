package models.question

import com.artclod.mathml.scalar.MathMLElem

trait AsciiMathML {
	val mathML: MathMLElem
	val rawStr: String
}