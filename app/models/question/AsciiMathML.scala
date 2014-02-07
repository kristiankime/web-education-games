package models.question

import mathml.scalar.MathMLElem

trait AsciiMathML {
	val mathML: MathMLElem
	val rawStr: String
}