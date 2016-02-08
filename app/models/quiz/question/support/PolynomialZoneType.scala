package models.quiz.question.support

object PolynomialZoneType {

  def apply(v: Short) = v match {
    case FirstDerivativeIncreasing.order => FirstDerivativeIncreasing
    case FirstDerivativeDecreasing.order => FirstDerivativeDecreasing
    case SecondDerivativeConcaveUp.order => SecondDerivativeConcaveUp
    case SecondDerivativeConcaveDown.order => SecondDerivativeConcaveDown
    case _ => throw new IllegalArgumentException("number " + v + " does not match a PolynomialZoneType")
  }

}

sealed trait PolynomialZoneType {
  val order : Short
  val positive : Boolean
  val display : String
}
object FirstDerivativeIncreasing extends PolynomialZoneType {
  val order : Short = 10
  val positive = true
  val display = "Increasing"
}
object FirstDerivativeDecreasing extends PolynomialZoneType {
  val order : Short = 11
  val positive = false
  val display = "Decreasing"
}
object SecondDerivativeConcaveUp  extends PolynomialZoneType {
  val order : Short = 20
  val positive = true
  val display = "Concave Up"
}
object SecondDerivativeConcaveDown  extends PolynomialZoneType {
  val order : Short = 21
  val positive = false
  val display = "Concave Down"
}