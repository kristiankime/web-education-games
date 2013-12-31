package models.mapper

import scala.slick.lifted.MappedTypeMapper
import mathml.Math
import mathml.MathML
import mathml.scalar.`0`
import mathml.scalar.MathMLElem
import mathml.scalar.Cn

object MathMLMapper {

	implicit def string2mathML = MappedTypeMapper.base[MathMLElem, String](
		mathML => mathML.toString,
		string => MathML(string).getOrElse(Math(Cn(-123456))))

}