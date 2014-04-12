package mathml.scalar

import mathml.MathML
import mathml.scalar.concept.ConstantDecimal

object Pi extends ConstantDecimal("pi", MathML.h.attributes, true, BigDecimal(math.Pi), Seq(): _*)