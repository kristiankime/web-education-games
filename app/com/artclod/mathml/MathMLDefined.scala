package com.artclod.mathml

import com.artclod.mathml.scalar.MathMLElem
import scala.util.Failure
import scala.util.Success

object MathMLDefined {

  def isDefinedAt(function: MathMLElem, boundVariables: (String, Double)*) : Boolean =
    function.evalT(boundVariables: _*) match {
      case Failure(_) => false
      case Success(v) =>
        if (v.isNaN || v.isInfinite) { false }
        else { true }
    }

}
