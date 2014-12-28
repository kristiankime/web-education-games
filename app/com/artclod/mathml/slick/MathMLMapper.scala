package com.artclod.mathml.slick

import com.artclod.mathml.scalar.{Cn, MathMLElem}
import com.artclod.mathml.{Math, MathML}
import play.api.db.slick.Config.driver.simple._

object MathMLMapper {

	implicit def string2mathML = MappedColumnType.base[MathMLElem, String](
		mathML => mathML.toString,
		string => MathML(string).getOrElse(Math(Cn(-123456))))

}