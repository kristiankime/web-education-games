package models.quiz

import com.artclod.mathml.scalar.MathMLElem

trait ViewableMath {
	val mathML: MathMLElem
	val rawStr: String
}