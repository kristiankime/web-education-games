package models.question.derivative.table

import scala.slick.lifted.MappedTypeMapper
import com.artclod.mathml.Math
import com.artclod.mathml.MathML
import com.artclod.mathml.scalar.MathMLElem
import com.artclod.mathml.scalar.Cn

object MathMLMapper {

	implicit def string2mathML = MappedTypeMapper.base[MathMLElem, String](
		mathML => mathML.toString,
		string => MathML(string).getOrElse(Math(Cn(-123456))))

}