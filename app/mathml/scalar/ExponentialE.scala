package mathml.scalar

import mathml.MathML
import mathml.scalar.concept.ConstantDecimal

object ExponentialE extends ConstantDecimal("exponentiale", MathML.h.attributes, true, BigDecimal(math.E), Seq(): _*) {
	override val c = Some(this);

	override val s = this;
}