package mathml.scalar

import mathml.MathML
import mathml.scalar.concept.ConstantDecimal

object ExponentialE extends ConstantDecimal("exponentiale", MathML.h.attributes, true, BigDecimal(math.E), Seq(): _*)